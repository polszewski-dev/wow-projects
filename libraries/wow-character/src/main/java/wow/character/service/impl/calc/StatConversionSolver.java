package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedCasterStats;
import wow.character.model.snapshot.AccumulatedOwnerStats;
import wow.character.model.snapshot.AccumulatedPartialStats;
import wow.character.util.StatConversionConditionChecker;
import wow.commons.model.effect.component.StatConversion;
import wow.commons.model.effect.component.StatConversionType;

import java.util.List;
import java.util.Map;
import java.util.function.DoubleSupplier;

import static java.util.stream.Collectors.groupingBy;
import static wow.commons.model.effect.component.StatConversionType.*;

/**
 * User: POlszewski
 * Date: 2026-11-24
 */
public class StatConversionSolver {
	private final Character owner;
	private final AccumulatedPartialStats stats;
	private final List<StatConversion> conversions;

	public StatConversionSolver(Character owner, AccumulatedPartialStats stats, List<StatConversion> conversions) {
		this.owner = owner;
		this.stats = stats;
		this.conversions = conversions;
	}

	public void solve() {
		if (stats instanceof AccumulatedCasterStats casterStats) {
			if (iAmAlone()) {
				performOwnerConversions(casterStats);
			} else if (iHaveAPet()) {
				performMasterConversions(owner, casterStats);
			} else {
				performPetConversions(owner, casterStats);
			}
		} else if (stats instanceof AccumulatedOwnerStats ownerStats) {
			if (iAmAlone() || iHaveAPet()) {
				performOwnerConversions(ownerStats);
			} else {
				performPetConversions(owner, ownerStats);
			}
		}
	}

	private void performOwnerConversions(AccumulatedCasterStats ownerStats) {
		var ownerConversions = byType(conversions);

		performSelfConversions(ownerStats, ownerConversions);
	}

	private void performMasterConversions(Character master, AccumulatedCasterStats masterStats) {
		var pet = master.getActivePet();
		var petStats = AccumulatedCasterStats.withTheSameParams(pet, masterStats);
		var petConversionList = accumulateEffectsWithoutConversions(pet, petStats);

		var masterConversions = byType(conversions);
		var petConversions = byType(petConversionList);

		performSelfConversions(masterStats, masterConversions);

		perform(MASTER_STAMINA_TO_STAMINA, petConversions, masterStats::getTotalStamina, petStats);
		perform(MASTER_INTELLECT_TO_INTELLECT, petConversions, masterStats::getTotalIntellect, petStats);

		perform(PET_STAMINA_TO_SPELL_DAMAGE, masterConversions, petStats::getTotalStamina, masterStats);
		perform(PET_INTELLECT_TO_SPELL_DAMAGE, masterConversions, petStats::getTotalIntellect, masterStats);
	}

	private void performPetConversions(Character pet, AccumulatedCasterStats petStats) {
		var master = pet.getMaster();
		var masterStats = AccumulatedCasterStats.withTheSameParams(master, petStats);
		var masterConversionList = accumulateEffectsWithoutConversions(master, masterStats);

		var petConversions = byType(conversions);
		var masterConversions = byType(masterConversionList);

		perform(MASTER_STAMINA_TO_STAMINA, petConversions, masterStats::getTotalStamina, petStats);
		perform(MASTER_INTELLECT_TO_INTELLECT, petConversions, masterStats::getTotalIntellect, petStats);

		perform(PET_STAMINA_TO_SPELL_DAMAGE, masterConversions, petStats::getTotalStamina, masterStats);
		perform(PET_INTELLECT_TO_SPELL_DAMAGE, masterConversions, petStats::getTotalIntellect, masterStats);

		performSelfConversions(masterStats, masterConversions);
		performSelfConversions(petStats, petConversions);

		perform(MASTER_POWER_TO_SPELL_DAMAGE, petConversions, masterStats::getPower, petStats);
		perform(MASTER_HIT_PCT_TO_HIT_PCT, petConversions, masterStats::getHitPct, petStats);
		perform(MASTER_HIT_RATING_TO_HIT_RATING, petConversions, masterStats::getHitRating, petStats);
	}

	private void performOwnerConversions(AccumulatedOwnerStats ownerStats) {
		var ownerConversions = byType(conversions);

		performSelfConversions(ownerStats, ownerConversions);
	}

	private void performPetConversions(Character pet, AccumulatedOwnerStats petStats) {
		var master = pet.getMaster();
		var masterStats = AccumulatedOwnerStats.forBaseStats(master);

		accumulateEffectsWithoutConversions(master, masterStats);

		var petConversions = byType(conversions);

		perform(MASTER_STAMINA_TO_STAMINA, petConversions, masterStats::getTotalStamina, petStats);
		perform(MASTER_INTELLECT_TO_INTELLECT, petConversions, masterStats::getTotalIntellect, petStats);

		performSelfConversions(petStats, petConversions);
	}

	private static void performSelfConversions(AccumulatedCasterStats myStats, Map<StatConversionType, List<StatConversion>> myConversions) {
		perform(OWNER_INTELLECT_TO_SPELL_POWER, myConversions, myStats::getTotalIntellect, myStats);
		perform(OWNER_INTELLECT_TO_SPELL_DAMAGE, myConversions, myStats::getTotalIntellect, myStats);
		perform(OWNER_INTELLECT_TO_SPELL_HEALING, myConversions, myStats::getTotalIntellect, myStats);
		perform(OWNER_SPIRIT_TO_SPELL_POWER, myConversions, myStats::getTotalSpirit, myStats);
	}

	private static void performSelfConversions(AccumulatedOwnerStats myStats, Map<StatConversionType, List<StatConversion>> myConversions) {
		perform(OWNER_INTELLECT_TO_MP5, myConversions, myStats::getTotalIntellect, myStats);
	}

	private boolean iAmAlone() {
		return owner.getActivePet() == null && !owner.isPet();
	}

	private boolean iHaveAPet() {
		return owner.getActivePet() != null;
	}

	private static void perform(
			StatConversionType conversionType,
			Map<StatConversionType, List<StatConversion>> conversionsByType,
			DoubleSupplier fromSupplier,
			AccumulatedPartialStats toStats
	) {
		var conversions = conversionsByType.get(conversionType);

		if (conversions == null) {
			return;
		}

		var from = fromSupplier.getAsDouble();

		for (var conversion : conversions) {
			toStats.accumulateConvertedAttribute(conversion, from);
		}
	}

	private Map<StatConversionType, List<StatConversion>> byType(List<StatConversion> conversions) {
		if (conversions == null) {
			return Map.of();
		}

		return conversions.stream()
				.filter(this::toConditionMatches)
				.collect(groupingBy(StatConversion::type));
	}

	private boolean toConditionMatches(StatConversion statConversion) {
		var checker = new StatConversionConditionChecker(stats.getActionType(), stats.getPowerType());

		return checker.check(statConversion);
	}

	private List<StatConversion> accumulateEffectsWithoutConversions(Character character, AccumulatedPartialStats baseStats) {
		var collector = Calculations.accumulateEffects(character, baseStats, false);

		return collector.statConversions;
	}
}
