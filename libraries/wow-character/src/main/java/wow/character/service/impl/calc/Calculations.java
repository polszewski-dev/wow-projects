package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.character.PetCharacter;
import wow.character.model.snapshot.*;
import wow.character.util.AbstractEffectCollector;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.StatConversion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.Math.max;
import static wow.character.model.snapshot.AttributePredicates.OWNER_OR_AURAS;
import static wow.character.model.snapshot.AttributePredicates.PET_ONLY;
import static wow.character.service.impl.calc.BaseStatsCalculations.newAccumulatedBaseStats;
import static wow.character.util.StatConversionConditionChecker.check;

/**
 * User: POlszewski
 * Date: 2026-10-25
 */
public abstract class Calculations {
	static double addAndMultiplyByPct(double value, double modifier, double modifierPct) {
		return max(value + modifier, 0) * max(1 + modifierPct / 100, 0);
	}

	static double multiplyByRatio(double value, double ratioPct, double increasePct) {
		return value * max(ratioPct / 100.0, 0) * max(1 + increasePct / 100.0, 0);
	}

	static void accumulateEffects(Character character, AccumulatedPartialStats stats) {
		accumulateEffects(character, stats, true);
	}

	private static DefaultEffectCollector accumulateEffects(Character character, AccumulatedPartialStats stats, boolean performConversions) {
		var collector = new DefaultEffectCollector(character, OWNER_OR_AURAS, stats);

		collector.solveAll();

		if (character instanceof PetCharacter petCharacter) {
			var masterCollector = new DefaultEffectCollector(petCharacter.getMaster(), PET_ONLY, stats);

			masterCollector.solveAll();
		}

		if (performConversions) {
			collector.solveStatConversions();
		}

		return collector;
	}

	private static class DefaultEffectCollector extends AbstractEffectCollector.OnlyEffects {
		final AccumulatedPartialStats stats;
		final Predicate<Attribute> attributePredicate;
		List<StatConversion> statConversions;

		DefaultEffectCollector(Character character, Predicate<Attribute> attributePredicate, AccumulatedPartialStats stats) {
			super(character);
			this.stats = stats;
			this.attributePredicate = attributePredicate;
		}

		@Override
		public void addEffect(Effect effect, int numStacks) {
			if (effect.hasAugmentedAbilities()) {
				return;
			}

			if (effect.hasModifierComponent()) {
				var modifierAttributeList = effect.getModifierAttributeList();
				stats.accumulateAttributes(modifierAttributeList, numStacks, attributePredicate);
			}

			if (effect.hasStatConversions()) {
				addStatConversions(effect);
			}
		}

		private void addStatConversions(Effect effect) {
			if (statConversions == null) {
				statConversions = new ArrayList<>();
			}
			statConversions.addAll(effect.getStatConversions());
		}

		void solveStatConversions() {
			var solver = newStatsConversionSolver();

			if (solver != null) {
				solver.solve();
			}
		}

		StatConversionSolver newStatsConversionSolver() {
			if (statConversions == null) {
				return null;
			}

			return new StatConversionSolver(character, stats, statConversions);
		}
	}

	private static class StatConversionSolver {
		private final PetCharacter pet;
		private final Character master;

		private final AccumulatedPartialStats stats;
		private final List<StatConversion> conversions;

		private AccumulatedBaseStats petsBaseStats;
		private AccumulatedBaseStats mastersBaseStats;
		private AccumulatedSpellStats mastersSpellStats;

		private final List<StatConversion> ownersBaseStatsAvailable = new ArrayList<>();
		private final List<StatConversion> petsBaseStatsRequired = new ArrayList<>();
		private final List<StatConversion> mastersBaseStatsRequired = new ArrayList<>();
		private final List<StatConversion> mastersHitStatsRequired = new ArrayList<>();
		private final List<StatConversion> mastersSpellStatsRequired = new ArrayList<>();

		StatConversionSolver(Character character, AccumulatedPartialStats stats, List<StatConversion> conversions) {
			this.pet = character.getActivePet();
			this.master = character.isPet() ? character.getMaster() : null;
			this.stats = stats;
			this.conversions = conversions;
		}

		void solve() {
			moveToTheCorrectLists(conversions);

			stats.accumulateConvertedAttributes(ownersBaseStatsAvailable);

			if (!petsBaseStatsRequired.isEmpty()) {
				calcPetsBaseStats();

				stats.accumulateConvertedAttributes(petsBaseStatsRequired, petsBaseStats);
			}

			if (!mastersBaseStatsRequired.isEmpty()) {
				calcMastersBaseStats();

				stats.accumulateConvertedAttributes(mastersBaseStatsRequired, mastersBaseStats);
			}

			if (!mastersSpellStatsRequired.isEmpty()) {
				calcMastersSpellStats();

				stats.accumulateConvertedAttributes(mastersSpellStatsRequired, mastersSpellStats);
			}

			if (!mastersHitStatsRequired.isEmpty()) {
				var mastersHitStats = calcMasterHitStats();

				stats.accumulateConvertedAttributes(mastersHitStatsRequired, mastersHitStats);
			}
		}

		private void calcPetsBaseStats() {
			if (petsBaseStats != null) {
				return;
			}

			this.petsBaseStats = newAccumulatedBaseStats(pet);

			var petConversionSolver = accumulateEffectsWithoutConversions(pet, petsBaseStats);

			if (petConversionSolver != null) {
				petConversionSolver.setMastersBaseStats(stats);
				petConversionSolver.solve();
			}
		}

		private void calcMastersBaseStats() {
			if (mastersBaseStats != null) {
				return;
			}

			this.mastersBaseStats = newAccumulatedBaseStats(master);

			var masterConversionSolver = accumulateEffectsWithoutConversions(master, mastersBaseStats);

			if (masterConversionSolver != null) {
				masterConversionSolver.setPetsBaseStats(stats);
				masterConversionSolver.solve();
			}
		}

		private void calcMastersSpellStats() {
			if (mastersSpellStats != null) {
				return;
			}

			var conditionArgs = stats.getConditionArgs().withCaster(master);
			this.mastersSpellStats = new AccumulatedSpellStats(conditionArgs);

			var masterConversionSolver = accumulateEffectsWithoutConversions(master, mastersSpellStats);

			if (masterConversionSolver != null) {
				masterConversionSolver.setPetsBaseStats(stats);
				masterConversionSolver.solve();
			}
		}

		private StatConversionSolver accumulateEffectsWithoutConversions(Character character, AccumulatedBaseStats baseStats) {
			var collector = accumulateEffects(character, baseStats, false);

			return collector.newStatsConversionSolver();
		}

		private void setPetsBaseStats(AccumulatedPartialStats petStats) {
			if (petStats instanceof AccumulatedBaseStats baseStats) {
				this.petsBaseStats = baseStats;
			}
		}

		private void setMastersBaseStats(AccumulatedPartialStats mastersStats) {
			if (mastersStats instanceof AccumulatedBaseStats baseStats) {
				this.mastersBaseStats = baseStats;
			}
		}

		private AccumulatedHitStats calcMasterHitStats() {
			var masterConditionArgs = stats.getConditionArgs().withCaster(master);
			var masterHitStats = new AccumulatedHitStats(masterConditionArgs);

			accumulateEffects(master, masterHitStats, false);
			return masterHitStats;
		}

		private void moveToTheCorrectLists(List<StatConversion> conversions) {
			for (var conversion : conversions) {
				if (toConditionMatches(conversion)) {
					addToTheCorrectList(conversion);
				}
			}
		}

		private boolean toConditionMatches(StatConversion statConversion) {
			var conditionArgs = stats.getConditionArgs();

			return check(statConversion, conditionArgs);
		}

		private void addToTheCorrectList(StatConversion conversion) {
			switch (conversion.type()) {
				case OWNER_INTELLECT_TO_SPELL_POWER, OWNER_INTELLECT_TO_SPELL_DAMAGE, OWNER_INTELLECT_TO_SPELL_HEALING, OWNER_SPIRIT_TO_SPELL_POWER -> {
					if ((stats instanceof AccumulatedSpellStats || stats instanceof AccumulatedCostStats)) {
						ownersBaseStatsAvailable.add(conversion);
					}
				}

				case PET_STAMINA_TO_SPELL_DAMAGE, PET_INTELLECT_TO_SPELL_DAMAGE -> {
					if (pet != null && stats instanceof AccumulatedSpellStats) {
						petsBaseStatsRequired.add(conversion);
					}
				}

				case MASTER_POWER_TO_SPELL_DAMAGE, MASTER_POWER_TO_ATTACK_POWER -> {
					if (master != null && stats instanceof AccumulatedSpellStats) {
						mastersSpellStatsRequired.add(conversion);
					}
				}

				case OWNER_INTELLECT_TO_MP5 -> {
					if (stats instanceof AccumulatedRegenStats) {
						ownersBaseStatsAvailable.add(conversion);
					}
				}

				case MASTER_STAMINA_TO_STAMINA, MASTER_INTELLECT_TO_INTELLECT -> {
					if (master != null && stats instanceof AccumulatedBaseStats) {
						mastersBaseStatsRequired.add(conversion);
					}
				}

				case MASTER_HIT_PCT_TO_HIT_PCT, MASTER_HIT_RATING_TO_HIT_RATING -> {
					if (master != null && stats instanceof AccumulatedHitStats) {
						mastersHitStatsRequired.add(conversion);
					}
				}

				case OWNER_INTELLECT_TO_ARMOR -> {
					// ignored
				}

				default -> throw new IllegalArgumentException("" + conversion.type());
			}
		}
	}

	static class AccumulatedSingleStat extends AccumulatedPartialStats {
		private final AttributeId attributeId;
		double totalValue;

		AccumulatedSingleStat(AttributeId attributeId, AttributeConditionArgs conditionArgs) {
			super(conditionArgs);
			this.attributeId = attributeId;
		}

		@Override
		public void accumulateAttribute(AttributeId id, double value) {
			if (id == attributeId) {
				this.totalValue += value;
			}
		}
	}
}
