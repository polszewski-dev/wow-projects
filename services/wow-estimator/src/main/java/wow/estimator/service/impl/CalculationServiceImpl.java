package wow.estimator.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.Buffs;
import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedBaseStats;
import wow.character.model.snapshot.BaseStatsSnapshot;
import wow.character.model.snapshot.StatSummary;
import wow.character.service.CharacterCalculationService;
import wow.character.service.CharacterService;
import wow.commons.model.Duration;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffCategory;
import wow.commons.model.buff.BuffNameRank;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.component.DirectComponent;
import wow.estimator.model.*;
import wow.estimator.service.CalculationService;
import wow.estimator.util.*;

import java.util.List;
import java.util.stream.Stream;

import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@Service
@AllArgsConstructor
public class CalculationServiceImpl implements CalculationService {
	private final CharacterCalculationService characterCalculationService;
	private final CharacterService characterService;
	private final SpecialAbilitySolver specialAbilitySolver;

	@Override
	public double getTalentSpEquivalent(String talentName, Player player) {
		var copy = player.copy();

		copy.getTalents().removeTalent(talentName);
		characterService.updateAfterRestrictionChange(copy);

		var baseEffectList = EffectList.createSolved(copy);
		var rotationStats = getAccumulatedRotationStats(player, player.getRotation());
		var targetDps = getRotationDps(player, player.getRotation(), rotationStats);

		var finder = StatEquivalentFinder.forTargetDps(
				baseEffectList, targetDps, copy, copy.getRotation(), this
		);

		return finder.getDpsStatEquivalent();
	}

	@Override
	public double getRotationDps(Player player, Rotation rotation, EffectList effectList, EffectList targetEffectList) {
		var rotationStats = getAccumulatedRotationStats(player, rotation, effectList, targetEffectList);

		return getRotationDps(player, rotation, rotationStats);
	}

	@Override
	public double getRotationDps(Player player, Rotation rotation, AccumulatedRotationStats rotationStats) {
		var calculator = new RotationDpsCalculator(
				rotation,
				ability -> getSnapshot(player, ability, rotationStats)
		);

		calculator.calculate();
		return calculator.getDps();
	}

	@Override
	public AccumulatedRotationStats getAccumulatedRotationStats(Player player, Rotation rotation) {
		var effectList = EffectList.createSolved(player);
		var targetEffectList = EffectList.createSolvedForTarget(player);

		return getAccumulatedRotationStats(player, rotation, effectList, targetEffectList);
	}

	@Override
	public AccumulatedRotationStats getAccumulatedRotationStats(Player player, Rotation rotation, EffectList effectList, EffectList targetEffectList) {
		var result = new AccumulatedRotationStats();
		var baseStats = getAccumulatedBaseStats(player, effectList, result);

		result.setBaseStats(baseStats);

		for (var ability : rotation.getAbilities()) {
			var abilityStats = getAccumulatedDamagingAbilityStats(player, ability, effectList, targetEffectList);

			result.addStats(ability, abilityStats);
		}

		return result;
	}

	private AccumulatedBaseStats getAccumulatedBaseStats(Player player, EffectList effectList, NonModifierHandler nonModifierHandler) {
		var stats = characterCalculationService.newAccumulatedBaseStats(player);
		effectList.accumulateAttributes(stats, nonModifierHandler);
		return stats;
	}

	private AccumulatedDamagingAbilityStats getAccumulatedDamagingAbilityStats(Player player, Ability ability, EffectList effectList, EffectList targetEffectList) {
		var stats = newAccumulatedDamagingAbilityStats(player, ability);
		effectList.accumulateAttributes(stats, null);
		targetEffectList.accumulateAttributes(stats.getTarget(), null);
		return stats;
	}

	private AccumulatedDamagingAbilityStats newAccumulatedDamagingAbilityStats(Player player, Ability ability) {
		var stats = new AccumulatedDamagingAbilityStats(player);

		var target = player.getTarget();
		var directComponent = getDamagingDirectComponent(ability);
		var periodicComponent = getDamagingPeriodicComponent(ability);

		stats.setAbility(ability);
		stats.setDirectComponent(directComponent);
		stats.setPeriodicComponent(periodicComponent);

		var castStats = characterCalculationService.newAccumulatedCastStats(player, ability, target);
		var costStats = characterCalculationService.newAccumulatedCostStats(player, ability, target);
		var targetStats = characterCalculationService.newAccumulatedTargetStats(player, ability, SPELL_DAMAGE, ability.getSchool());
		var hitStats = characterCalculationService.newAccumulatedHitStats(player, ability, target);

		stats.setCast(castStats);
		stats.setCost(costStats);
		stats.setTarget(targetStats);
		stats.setHit(hitStats);

		if (directComponent != null) {
			var directComponentStats = characterCalculationService.newAccumulatedDirectComponentStats(player, ability, target, SPELL_DAMAGE, directComponent);

			stats.setDirect(directComponentStats);
		}

		if (ability.getAppliedEffect() != null) {
			var periodicStats = characterCalculationService.newAccumulatedPeriodicComponentStats(player, ability, target, SPELL_DAMAGE, periodicComponent);
			var effectDurationStats = characterCalculationService.newAccumulatedDurationStats(player, ability, target);
			var receivedEffectStats = characterCalculationService.newAccumulatedReceivedEffectStats(target, ability);

			stats.setPeriodic(periodicStats);
			stats.setEffectDuration(effectDurationStats);
			stats.setReceivedEffectStats(receivedEffectStats);
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

		var effect = effectApplication.effect();

		if (effect.hasPeriodicComponent(ComponentType.DAMAGE)) {
			return effect.getPeriodicComponent();
		}

		return null;
	}

	@Override
	public RotationStats getRotationStats(Player player, Rotation rotation) {
		var rotationStats = getAccumulatedRotationStats(player, rotation);

		var calculator = new RotationStatsCalculator(
				rotation,
				ability -> getSnapshot(player, ability, rotationStats.copy())
		);

		calculator.calculate();
		return calculator.getStats();
	}

	private Snapshot getSnapshot(Player player, Ability ability) {
		var rotationStats = getAccumulatedRotationStats(player, Rotation.onlyFiller(ability));

		return getSnapshot(player, ability, rotationStats);
	}

	private Snapshot getSnapshot(Player player, Ability ability, AccumulatedRotationStats rotationStats) {
		var abilityStats = rotationStats.get(ability.getAbilityId());
		var baseStats = rotationStats.getBaseStats();
		var target = player.getTarget();
		var snapshot = new Snapshot();

		snapshot.setAbility(ability);

		var base = characterCalculationService.getBaseStatsSnapshot(player, baseStats);

		snapshot.setBase(base);
		abilityStats.solveStatConversions(rotationStats.getStatConversions(), base);
		calculateDamagingSpellStats(player, ability, abilityStats, target, snapshot, base);

		var recalculate = specialAbilitySolver.solveAbilities(snapshot, abilityStats, rotationStats, player);

		if (recalculate) {
			calculateDamagingSpellStats(player, ability, abilityStats, target, snapshot, base);
		}

		return snapshot;
	}

	private void calculateDamagingSpellStats(Player player, Ability ability, AccumulatedDamagingAbilityStats abilityStats, Character target, Snapshot snapshot, BaseStatsSnapshot base) {
		var cast = characterCalculationService.getSpellCastSnapshot(player, ability, abilityStats.getCast());
		var cost = characterCalculationService.getSpellCostSnapshot(player, ability, abilityStats.getCost());
		var hitPct = characterCalculationService.getSpellHitPct(player, ability, target, abilityStats.getHit());

		snapshot.setCast(cast);
		snapshot.setCost(cost);
		snapshot.setHitPct(hitPct);

		var targetStats = abilityStats.getTarget();

		if (abilityStats.getDirectComponent() != null) {
			var direct = characterCalculationService.getDirectSpellComponentSnapshot(player, ability, target, abilityStats.getDirectComponent(), base, abilityStats.getDirect(), targetStats);

			snapshot.setDirect(direct);
		}

		if (ability.getAppliedEffect() != null) {
			var periodic = characterCalculationService.getPeriodicComponentSnapshot(player, ability, target, abilityStats.getPeriodic(), targetStats);
			var effectDuration = characterCalculationService.getEffectDurationSnapshot(player, ability, abilityStats.getEffectDuration(), abilityStats.getReceivedEffectStats());

			snapshot.setPeriodic(periodic);
			snapshot.setEffectDuration(effectDuration);
		}
	}

	@Override
	public AbilityStats getAbilityStats(Player player, Ability ability, boolean usesCombatRatings, double equivalentAmount) {
		var snapshot = getSnapshot(player, ability);
		var totalDamage = snapshot.getTotalDamage();
		var effectiveCastTime = snapshot.getEffectiveCastTime();
		var manaCost = snapshot.getManaCost();

		return new AbilityStats(
				player,
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
				getStatEquivalent(player, ability, HIT_RATING, HIT_PCT, usesCombatRatings, equivalentAmount),
				getStatEquivalent(player, ability, CRIT_RATING, CRIT_PCT, usesCombatRatings, equivalentAmount),
				getStatEquivalent(player, ability, HASTE_RATING, HASTE_PCT, usesCombatRatings, equivalentAmount),
				snapshot.getDuration(),
				snapshot.getCooldown(),
				0,
				0
		);
	}

	private double getStatEquivalent(Player player, Ability ability, AttributeId ratingStat, AttributeId pctStat, boolean usesCombatRatings, double equivalentAmount) {
		var stat = usesCombatRatings ? ratingStat : pctStat;

		var specialAbility = SpecialAbility.of(
				EffectImpl.newAttributeEffect(
						Attributes.of(stat, equivalentAmount)
				)
		);

		var finder = StatEquivalentFinder.forAdditionalSpecialAbility(
				specialAbility,
				player,
				Rotation.onlyFiller(ability),
				this
		);

		return finder.getDpsStatEquivalent();
	}

	@Override
	public StatSummary getCurrentStats(Player player) {
		return getStats(player);
	}

	@Override
	public StatSummary getStats(Player player, BuffCategory... buffCategories) {
		var copy = player.copy();
		copy.getBuffs().set(getFilteredBuffs(player.getBuffs(), buffCategories));
		return getStats(copy);
	}

	private List<BuffNameRank> getFilteredBuffs(Buffs buffs, BuffCategory[] buffCategories) {
		return buffs.getStream()
				.filter(x -> Stream.of(buffCategories).anyMatch(y -> x.getCategories().contains(y)))
				.map(Buff::getNameRank)
				.toList();
	}

	@Override
	public StatSummary getEquipmentStats(Player player) {
		var copy = player.copy();

		copy.resetBuild();
		copy.resetBuffs();
		characterService.updateAfterRestrictionChange(copy);

		var withEquipment = getStats(copy);

		copy.resetEquipment();

		var withoutEquipment = getStats(copy);

		return withEquipment.difference(withoutEquipment);
	}

	private StatSummary getStats(Player player) {
		return characterCalculationService.getStatSummary(player);
	}

	@Override
	public SpecialAbilityStats getSpecialAbilityStats(SpecialAbility specialAbility, Player player) {
		var spEquivalent = getSpEquivalent(specialAbility, player);

		return new SpecialAbilityStats(
				specialAbility,
				spEquivalent
		);
	}

	private double getSpEquivalent(SpecialAbility specialAbility, Player player) {
		var finder = StatEquivalentFinder.forReplacedSpecialAbility(
				specialAbility,
				player,
				player.getRotation(),
				this
		);

		return finder.getDpsStatEquivalent();
	}
}
