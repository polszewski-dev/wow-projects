package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.Rotation;
import wow.character.model.character.Buffs;
import wow.character.model.snapshot.AccumulatedBaseStats;
import wow.character.model.snapshot.BaseStatsSnapshot;
import wow.character.model.snapshot.StatSummary;
import wow.character.service.CharacterCalculationService;
import wow.character.service.CharacterService;
import wow.commons.model.Duration;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffCategory;
import wow.commons.model.buff.BuffIdAndRank;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.component.DirectComponent;
import wow.commons.model.talent.TalentId;
import wow.minmax.model.*;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.service.CalculationService;
import wow.minmax.service.impl.enumerator.RotationDpsCalculator;
import wow.minmax.service.impl.enumerator.RotationStatsCalculator;
import wow.minmax.service.impl.enumerator.SpecialAbilitySolver;
import wow.minmax.service.impl.enumerator.StatEquivalentFinder;
import wow.minmax.util.EffectList;
import wow.minmax.util.NonModifierHandler;

import java.util.List;
import java.util.stream.Stream;

import static wow.commons.model.attribute.AttributeId.*;
import static wow.minmax.model.config.CharacterFeature.COMBAT_RATINGS;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@Service
@AllArgsConstructor
public class CalculationServiceImpl implements CalculationService {
	private final CharacterCalculationService characterCalculationService;
	private final CharacterService characterService;
	private final MinmaxConfigRepository minmaxConfigRepository;
	private final SpecialAbilitySolver specialAbilitySolver;

	@Override
	public double getSpEquivalent(TalentId talentId, PlayerCharacter character) {
		var copy = character.copy();

		copy.getTalents().removeTalent(talentId);
		characterService.updateAfterRestrictionChange(copy);

		var baseEffectList = EffectList.createSolved(copy);
		var rotationStats = getAccumulatedRotationStats(character, character.getRotation());
		var targetDps = getRotationDps(character, character.getRotation(), rotationStats);

		var finder = StatEquivalentFinder.forTargetDps(
				baseEffectList, targetDps, copy, copy.getRotation(), this
		);

		return finder.getDpsStatEquivalent();
	}

	@Override
	public double getRotationDps(PlayerCharacter character, Rotation rotation, EffectList effectList, EffectList targetEffectList) {
		var rotationStats = getAccumulatedRotationStats(character, rotation, effectList, targetEffectList);

		return getRotationDps(character, rotation, rotationStats);
	}

	@Override
	public double getRotationDps(PlayerCharacter character, Rotation rotation, AccumulatedRotationStats rotationStats) {
		var calculator = new RotationDpsCalculator(
				rotation,
				ability -> getSnapshot(character, ability, rotationStats)
		);

		calculator.calculate();
		return calculator.getDps();
	}

	@Override
	public AccumulatedRotationStats getAccumulatedRotationStats(PlayerCharacter character, Rotation rotation) {
		var effectList = EffectList.createSolved(character);
		var targetEffectList = EffectList.createSolvedForTarget(character);

		return getAccumulatedRotationStats(character, rotation, effectList, targetEffectList);
	}

	@Override
	public AccumulatedRotationStats getAccumulatedRotationStats(PlayerCharacter character, Rotation rotation, EffectList effectList, EffectList targetEffectList) {
		var result = new AccumulatedRotationStats();
		var baseStats = getAccumulatedBaseStats(character, effectList, result);

		result.setBaseStats(baseStats);

		for (var ability : rotation.getAbilities()) {
			var abilityStats = getAccumulatedDamagingAbilityStats(character, ability, effectList, targetEffectList);

			result.addStats(ability, abilityStats);
		}

		return result;
	}

	private AccumulatedBaseStats getAccumulatedBaseStats(PlayerCharacter character, EffectList effectList, NonModifierHandler nonModifierHandler) {
		var stats = characterCalculationService.newAccumulatedBaseStats(character);
		effectList.accumulateAttributes(stats, nonModifierHandler);
		return stats;
	}

	private AccumulatedDamagingAbilityStats getAccumulatedDamagingAbilityStats(PlayerCharacter character, Ability ability, EffectList effectList, EffectList targetEffectList) {
		var stats = newAccumulatedDamagingAbilityStats(character, ability);
		effectList.accumulateAttributes(stats, null);
		targetEffectList.accumulateAttributes(stats.getTarget(), null);
		return stats;
	}

	private AccumulatedDamagingAbilityStats newAccumulatedDamagingAbilityStats(PlayerCharacter character, Ability ability) {
		var stats = new AccumulatedDamagingAbilityStats(character.getLevel());

		var target = character.getTarget();
		var directComponent = getDamagingDirectComponent(ability);
		var periodicComponent = getDamagingPeriodicComponent(ability);

		stats.setAbility(ability);
		stats.setDirectComponent(directComponent);
		stats.setPeriodicComponent(periodicComponent);

		var castStats = characterCalculationService.newAccumulatedCastStats(character, ability, target);
		var costStats = characterCalculationService.newAccumulatedCostStats(character, ability, target);
		var targetStats = characterCalculationService.newAccumulatedTargetStats(character, ability, PowerType.SPELL_DAMAGE, ability.getSchool());
		var hitStats = characterCalculationService.newAccumulatedHitStats(character, ability, target);

		stats.setCast(castStats);
		stats.setCost(costStats);
		stats.setTarget(targetStats);
		stats.setHit(hitStats);

		if (directComponent != null) {
			var directComponentStats = characterCalculationService.newAccumulatedDirectComponentStats(character, ability, target, directComponent);

			stats.setDirect(directComponentStats);
		}

		if (ability.getAppliedEffect() != null) {
			var periodicStats = characterCalculationService.newAccumulatedPeriodicComponentStats(character, ability, target, periodicComponent);
			var effectDurationStats = characterCalculationService.newAccumulatedDurationStats(character, ability, target);

			stats.setPeriodic(periodicStats);
			stats.setEffectDuration(effectDurationStats);
		}

		return stats;
	}

	private DirectComponent getDamagingDirectComponent(Ability ability) {
		return ability.getDirectComponents().stream()
				.filter(x -> x.type() == ComponentType.DAMAGE)
				.findAny()
				.orElse(null);
	}

	private PeriodicComponent getDamagingPeriodicComponent(Ability ability) {
		var effectApplication = ability.getEffectApplication();

		if (effectApplication == null) {
			return null;
		}

		var periodicComponent = effectApplication.effect().getPeriodicComponent();

		if (periodicComponent == null) {
			return null;
		}

		return periodicComponent.type() == ComponentType.DAMAGE ? periodicComponent : null;
	}

	@Override
	public RotationStats getRotationStats(PlayerCharacter character, Rotation rotation) {
		var rotationStats = getAccumulatedRotationStats(character, rotation);

		var calculator = new RotationStatsCalculator(
				rotation,
				ability -> getSnapshot(character, ability, rotationStats.copy())
		);

		calculator.calculate();
		return calculator.getStats();
	}

	private Snapshot getSnapshot(PlayerCharacter character, Ability ability) {
		var rotationStats = getAccumulatedRotationStats(character, Rotation.onlyFiller(ability));

		return getSnapshot(character, ability, rotationStats);
	}

	private Snapshot getSnapshot(PlayerCharacter character, Ability ability, AccumulatedRotationStats rotationStats) {
		var abilityStats = rotationStats.get(ability.getAbilityId());
		var baseStats = rotationStats.getBaseStats();
		var target = character.getTarget();
		var snapshot = new Snapshot();

		snapshot.setAbility(ability);

		var base = characterCalculationService.getBaseStatsSnapshot(character, baseStats);

		snapshot.setBase(base);
		abilityStats.solveStatConversions(rotationStats.getStatConversions(), base);
		calculateDamagingSpellStats(character, ability, abilityStats, target, snapshot, base);

		var recalculate = specialAbilitySolver.solveAbilities(snapshot, abilityStats, rotationStats, character);

		if (recalculate) {
			calculateDamagingSpellStats(character, ability, abilityStats, target, snapshot, base);
		}

		return snapshot;
	}

	private void calculateDamagingSpellStats(PlayerCharacter character, Ability ability, AccumulatedDamagingAbilityStats abilityStats, wow.character.model.character.Character target, Snapshot snapshot, BaseStatsSnapshot base) {
		var cast = characterCalculationService.getSpellCastSnapshot(character, ability, abilityStats.getCast());
		var cost = characterCalculationService.getSpellCostSnapshot(character, ability, abilityStats.getCost());
		var hitPct = characterCalculationService.getSpellHitPct(character, ability, target, abilityStats.getHit());

		snapshot.setCast(cast);
		snapshot.setCost(cost);
		snapshot.setHitPct(hitPct);

		var targetStats = abilityStats.getTarget();

		if (abilityStats.getDirectComponent() != null) {
			var direct = characterCalculationService.getDirectSpellDamageSnapshot(character, ability, target, abilityStats.getDirectComponent(), base, abilityStats.getDirect(), targetStats);

			snapshot.setDirect(direct);
		}

		if (ability.getAppliedEffect() != null) {
			var periodic = characterCalculationService.getPeriodicSpellDamageSnapshot(character, ability, target, abilityStats.getPeriodic(), targetStats);
			var effectDuration = characterCalculationService.getEffectDurationSnapshot(character, ability, target, abilityStats.getEffectDuration(), targetStats);

			snapshot.setPeriodic(periodic);
			snapshot.setEffectDuration(effectDuration);
		}
	}

	@Override
	public SpellStats getSpellStats(PlayerCharacter character, Ability ability) {
		var snapshot = getSnapshot(character, ability);
		var totalDamage = snapshot.getTotalDamage();
		var effectiveCastTime = snapshot.getEffectiveCastTime();
		var manaCost = snapshot.getManaCost();

		return new SpellStats(
				character,
				ability,
				totalDamage,
				totalDamage / effectiveCastTime,
				Duration.seconds(effectiveCastTime),
				snapshot.isInstantCast(),
				manaCost,
				totalDamage / manaCost,
				snapshot.getPower(),
				snapshot.getHitPct(),
				snapshot.getCritPct(),
				snapshot.getHastePct(),
				snapshot.getCoeffDirect(),
				snapshot.getCoeffDoT(),
				snapshot.getCritCoeff(),
				getStatEquivalent(character, ability, HIT_RATING, HIT_PCT),
				getStatEquivalent(character, ability, CRIT_RATING, CRIT_PCT),
				getStatEquivalent(character, ability, HASTE_RATING, HASTE_PCT),
				snapshot.getDuration(),
				snapshot.getCooldown(),
				0,
				0
		);
	}

	private double getStatEquivalent(PlayerCharacter character, Ability ability, AttributeId ratingStat, AttributeId pctStat) {
		var usesCombatRatings = minmaxConfigRepository.hasFeature(character, COMBAT_RATINGS);
		var stat = usesCombatRatings ? ratingStat : pctStat;
		var amount = minmaxConfigRepository.getViewConfig(character).orElseThrow().equivalentAmount();

		var specialAbility = SpecialAbility.of(
				EffectImpl.newAttributeEffect(
						Attributes.of(stat, amount)
				)
		);

		var finder = StatEquivalentFinder.forAdditionalSpecialAbility(
				specialAbility,
				character,
				Rotation.onlyFiller(ability),
				this
		);

		return finder.getDpsStatEquivalent();
	}

	@Override
	public StatSummary getCurrentStats(PlayerCharacter character) {
		return getStats(character);
	}

	@Override
	public StatSummary getStats(PlayerCharacter character, BuffCategory... buffCategories) {
		var copy = character.copy();
		copy.setBuffs(getFilteredBuffs(character.getBuffs(), buffCategories));
		return getStats(copy);
	}

	private List<BuffIdAndRank> getFilteredBuffs(Buffs buffs, BuffCategory[] buffCategories) {
		return buffs.getList().stream()
				.filter(x -> Stream.of(buffCategories).anyMatch(y -> x.getCategories().contains(y)))
				.map(Buff::getId)
				.toList();
	}

	@Override
	public StatSummary getEquipmentStats(PlayerCharacter character) {
		var copy = character.copy();

		copy.resetBuild();
		copy.resetBuffs();
		characterService.updateAfterRestrictionChange(copy);

		var withEquipment = getStats(copy);

		copy.resetEquipment();

		var withoutEquipment = getStats(copy);

		return withEquipment.difference(withoutEquipment);
	}

	private StatSummary getStats(PlayerCharacter character) {
		return characterCalculationService.getStatSummary(character);
	}

	@Override
	public SpecialAbilityStats getSpecialAbilityStats(SpecialAbility specialAbility, PlayerCharacter character) {
		var spEquivalent = getSpEquivalent(specialAbility, character);

		return new SpecialAbilityStats(
				specialAbility,
				spEquivalent
		);
	}

	private double getSpEquivalent(SpecialAbility specialAbility, PlayerCharacter character) {
		var finder = StatEquivalentFinder.forReplacedSpecialAbility(
				specialAbility,
				character,
				character.getRotation(),
				this
		);

		return finder.getDpsStatEquivalent();
	}
}
