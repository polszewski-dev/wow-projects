package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.snapshot.*;
import wow.character.service.CharacterCalculationService;
import wow.character.util.AbstractEffectCollector;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.Duration;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.effect.component.PeriodicComponent;
import wow.commons.model.effect.component.StatConversion;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Coefficient;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.spell.component.DirectComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.clamp;
import static java.lang.Math.max;
import static wow.character.util.AttributeConditionArgsUtil.*;
import static wow.commons.constant.SpellConstants.*;

/**
 * User: POlszewski
 * Date: 2023-04-27
 */
@Service
@AllArgsConstructor
public class CharacterCalculationServiceImpl implements CharacterCalculationService {
	@Override
	public AccumulatedBaseStats newAccumulatedBaseStats(Character character) {
		var conditionArgs = AttributeConditionArgs.forBaseStats(character);
		var stats = new AccumulatedBaseStats(conditionArgs);

		stats.accumulateBaseStatInfo(character.getBaseStatInfo());
		return stats;
	}

	@Override
	public AccumulatedCastStats newAccumulatedCastStats(Character character, Ability ability, Character target) {
		var conditionArgs = AttributeConditionArgs.forSpell(character, ability, target);

		return new AccumulatedCastStats(conditionArgs);
	}

	@Override
	public AccumulatedCostStats newAccumulatedCostStats(Character character, Ability ability, Character target) {
		var conditionArgs = AttributeConditionArgs.forSpell(character, ability, target);

		return new AccumulatedCostStats(conditionArgs);
	}

	@Override
	public AccumulatedTargetStats newAccumulatedTargetStats(Character target, Spell spell, PowerType powerType, SpellSchool school) {
		var conditionArgs = getTargetConditionArgs(target, spell, powerType);

		return new AccumulatedTargetStats(conditionArgs);
	}

	@Override
	public AccumulatedHitStats newAccumulatedHitStats(Character character, Spell spell, Character target) {
		var conditionArgs = getDamagingComponentConditionArgs(character, spell, target);

		return new AccumulatedHitStats(conditionArgs);
	}

	@Override
	public AccumulatedDurationStats newAccumulatedDurationStats(Character character, Spell spell, Character target) {
		var conditionArgs = getDamagingComponentConditionArgs(character, spell, target);

		return new AccumulatedDurationStats(conditionArgs);
	}

	@Override
	public AccumulatedSpellStats newAccumulatedDirectComponentStats(Character character, Spell spell, Character target, DirectComponent directComponent) {
		var conditionArgs = getDirectComponentConditionArgs(character, spell, target);

		conditionArgs.setSpellSchool(directComponent.school());

		return newAccumulatedSpellStats(character, conditionArgs);
	}

	@Override
	public AccumulatedSpellStats newAccumulatedPeriodicComponentStats(Character character, Spell spell, Character target, PeriodicComponent periodicComponent) {
		var conditionArgs = getPeriodicComponentConditionArgs(character, spell, target);

		conditionArgs.setSpellSchool(periodicComponent.school());

		return newAccumulatedSpellStats(character, conditionArgs);
	}

	private AccumulatedSpellStats newAccumulatedSpellStats(Character character, AttributeConditionArgs conditionArgs) {
		var spellStats = new AccumulatedSpellStats(conditionArgs);
		spellStats.accumulateBaseStatInfo(character.getBaseStatInfo());
		return spellStats;
	}

	@Override
	public BaseStatsSnapshot getBaseStatsSnapshot(Character character) {
		var stats = getAccumulatedBaseStats(character);

		return getBaseStatsSnapshot(character, stats);
	}

	private AccumulatedBaseStats getAccumulatedBaseStats(Character character) {
		var stats = newAccumulatedBaseStats(character);

		accumulateEffects(character, stats);
		return stats;
	}

	@Override
	public BaseStatsSnapshot getBaseStatsSnapshot(Character character, AccumulatedBaseStats stats) {
		var snapshot = new BaseStatsSnapshot();

		var strength = getBaseStat(stats, stats.getStrength(), stats.getStrengthPct());
		var agility = getBaseStat(stats, stats.getAgility(), stats.getAgilityPct());
		var stamina = getBaseStat(stats, stats.getStamina(), stats.getStaminaPct());
		var intellect = getBaseStat(stats, stats.getIntellect(), stats.getIntellectPct());
		var spirit = getBaseStat(stats, stats.getSpirit(), stats.getSpiritPct());
		var maxHealth = getMaxHealth(character.getBaseStatInfo(), stats, stamina);
		var maxMana = getMaxMana(character.getBaseStatInfo(), stats, intellect);

		snapshot.setStrength(strength);
		snapshot.setAgility(agility);
		snapshot.setStamina(stamina);
		snapshot.setIntellect(intellect);
		snapshot.setSpirit(spirit);
		snapshot.setMaxHealth(maxHealth);
		snapshot.setMaxMana(maxMana);

		return snapshot;
	}

	private int getBaseStat(AccumulatedBaseStats stats, double statPoints, double statPct) {
		var base = stats.getBaseStats() + statPoints;
		var basePct = stats.getBaseStatsPct() + statPct;

		return (int) (base * (1 + basePct / 100));
	}

	private int getMaxHealth(BaseStatInfo baseStatInfo, AccumulatedBaseStats stats, double stamina) {
		var healthBonus = HEALTH_PER_STAMINA * (stamina - baseStatInfo.getBaseStamina()) + stats.getMaxHealth();
		var healthBonusPct = stats.getMaxHealthPct();

		return (int) addAndMultiplyByPct(0, healthBonus, healthBonusPct);
	}

	private int getMaxMana(BaseStatInfo baseStatInfo, AccumulatedBaseStats stats, double intellect) {
		var manaBonus = MANA_PER_INTELLECT * (intellect - baseStatInfo.getBaseIntellect()) + stats.getMaxMana();
		var manaBonusPct = stats.getMaxManaPct();

		return (int) addAndMultiplyByPct(0, manaBonus, manaBonusPct);
	}

	@Override
	public SpellCastSnapshot getSpellCastSnapshot(Character character, Ability ability, Character target) {
		var castStats = getAccumulatedCastStats(character, ability, target);

		return getSpellCastSnapshot(character, ability, castStats);
	}

	private AccumulatedCastStats getAccumulatedCastStats(Character character, Ability ability, Character target) {
		var castStats = newAccumulatedCastStats(character, ability, target);

		accumulateEffects(character, castStats);
		return castStats;
	}

	@Override
	public SpellCastSnapshot getSpellCastSnapshot(Character character, Ability ability, AccumulatedCastStats castStats) {
		var snapshot = new SpellCastSnapshot();

		var hastePct = getHastePct(character, castStats);
		var castTime = getCastTime(ability, castStats, hastePct);
		var gcd = getGcd(hastePct);

		snapshot.setHastePct(hastePct);
		snapshot.setCastTime(castTime);
		snapshot.setGcd(gcd);

		return snapshot;
	}

	private double getHastePct(Character character, AccumulatedCastStats castStats) {
		return getHastePct(character, castStats.getHastePct(), castStats.getHasteRating());
	}

	private double getHastePct(Character character, AccumulatedDurationStats durationStats) {
		return getHastePct(character, durationStats.getHastePct(), durationStats.getHasteRating());
	}

	private double getHastePct(Character character, double hastePct, double hasteRating) {
		var cr = character.getCombatRatingInfo();
		var ratingHastePct = hasteRating / cr.getSpellHaste();

		return hastePct + ratingHastePct;
	}

	private double getCastTime(Ability ability, AccumulatedCastStats castStats, double hastePct) {
		var modifiedCastTime = addAndMultiplyByPct(
				ability.getCastTime().getSeconds(),
				castStats.getCastTime(),
				castStats.getCastTimePct()
		);

		return getActualCastTime(modifiedCastTime, hastePct);
	}

	private double getGcd(double hastePct) {
		var gcd = GCD.getSeconds();
		var minGcd = MIN_GCD.getSeconds();

		return max(getActualCastTime(gcd, hastePct), minGcd);
	}

	private double getActualCastTime(double baseCastTime, double hastePct) {
		return baseCastTime / (1 + hastePct / 100);
	}

	@Override
	public SpellCostSnapshot getSpellCostSnapshot(Character character, Ability ability, Character target, BaseStatsSnapshot baseStats) {
		if (ability.getCost() == null) {
			return null;
		}

		var costStats = getAccumulatedCostStats(character, ability, target, baseStats);

		return getSpellCostSnapshot(character, ability, costStats);
	}

	private AccumulatedCostStats getAccumulatedCostStats(Character character, Ability ability, Character target, BaseStatsSnapshot baseStats) {
		var costStats = newAccumulatedCostStats(character, ability, target);

		accumulateEffects(character, costStats, baseStats);
		return costStats;
	}

	@Override
	public SpellCostSnapshot getSpellCostSnapshot(Character character, Ability ability, AccumulatedCostStats costStats) {
		if (ability.getCost() == null) {
			return null;
		}

		var snapshot = new SpellCostSnapshot();

		var costUnreduced = getCost(character, ability, costStats);
		var cost = (int)(costUnreduced * (1 - costStats.getCostReductionPct() / 100));
		var cooldown = getCooldown(ability, costStats);

		snapshot.setResourceType(ability.getCost().resourceType());
		snapshot.setCost(cost);
		snapshot.setCostUnreduced(costUnreduced);
		snapshot.setCooldown(cooldown);

		return snapshot;
	}

	private int getCost(Character character, Ability ability, AccumulatedCostStats costStats) {
		var cost = ability.getCost();
		var result = (double) cost.amount();

		if (!cost.baseStatPct().isZero()) {
			var baseStatValue = character.getBaseStatValue(cost.resourceType());
			var pct = cost.baseStatPct().getCoefficient();

			result += baseStatValue * pct;
		}

		if (!cost.coefficient().value().isZero()) {
			var coeff = cost.coefficient().value().getCoefficient();
			var power = costStats.getPower();

			result += power * coeff;
		}

		return (int) addAndMultiplyByPct(
				result,
				costStats.getCost(cost.resourceType()),
				costStats.getCostPct(cost.resourceType())
		);
	}

	private double getCooldown(Ability ability, AccumulatedCostStats costStats) {
		var spellCooldown = ability.getCooldown().getSeconds();
		var cooldown = costStats.getCooldown();
		var cooldownPct = costStats.getCooldownPct();

		return addAndMultiplyByPct(spellCooldown, cooldown, cooldownPct);
	}

	@Override
	public double getSpellHitPct(Character character, Spell spell, Character target) {
		var hitStats = getAccumulatedHitStats(character, spell, target);

		return getSpellHitPct(character, spell, target, hitStats);
	}

	private AccumulatedHitStats getAccumulatedHitStats(Character character, Spell spell, Character target) {
		var hitStats = newAccumulatedHitStats(character, spell, target);

		accumulateEffects(character, hitStats);
		return hitStats;
	}

	@Override
	public double getSpellHitPct(Character character, Spell spell, Character target, AccumulatedHitStats hitStats) {
		var levelDifference = Character.getLevelDifference(character, target);

		return getSpellHitPct(character, hitStats, levelDifference);
	}

	private double getSpellHitPct(Character character, AccumulatedHitStats hitStats, int levelDifference) {
		var totalHit = getSpellHitPctBonus(character, hitStats);

		var gameVersion = character.getGameVersion();
		var baseSpellHitChancePct = gameVersion.getBaseSpellHitChancePct(levelDifference);
		var maxSpellHitChancePct = gameVersion.getMaxPveSpellHitChancePct();

		return clamp(baseSpellHitChancePct + totalHit, 0, maxSpellHitChancePct);
	}

	private double getSpellHitPctBonus(Character character, AccumulatedHitStats hitStats) {
		var cr = character.getCombatRatingInfo();
		var ratingHit = hitStats.getHitRating() / cr.getSpellHit();
		var pctHit = hitStats.getHitPct();

		return ratingHit + pctHit;
	}

	@Override
	public EffectDurationSnapshot getEffectDurationSnapshot(Character character, Spell spell, Character target) {
		var durationStats = getAccumulatedDurationStats(character, spell, target);
		var targetStats = getAccumulatedTargetStats(spell, target, null, null, null);

		return getEffectDurationSnapshot(character, spell, target, durationStats, targetStats);
	}

	private AccumulatedDurationStats getAccumulatedDurationStats(Character character, Spell spell, Character target) {
		var durationStats = newAccumulatedDurationStats(character, spell, target);

		accumulateEffects(character, durationStats);
		return durationStats;
	}

	@Override
	public EffectDurationSnapshot getEffectDurationSnapshot(Character character, Spell spell, Character target, AccumulatedDurationStats durationStats, AccumulatedTargetStats targetStats) {
		var effectApplication = spell.getEffectApplication();
		var effect = effectApplication.effect();

		var durationSnapshot = new EffectDurationSnapshot();

		if (spell instanceof Ability ability && ability.isChanneled()) {
			var hastePct = getHastePct(character, durationStats);
			var baseDuration = effectApplication.duration().getSeconds();
			var actualChannelTime = getActualCastTime(baseDuration, hastePct);
			var baseTickInterval = effect.getTickInterval().getSeconds();
			var numTicks = (int) (baseDuration / baseTickInterval);
			var tickInterval = actualChannelTime / numTicks;

			durationSnapshot.setDuration(Duration.seconds(actualChannelTime));
			durationSnapshot.setTickInterval(Duration.seconds(tickInterval));
		} else {
			var baseDuration = effectApplication.duration();
			var duration = getEffectDuration(baseDuration, durationStats, targetStats);
			var tickInterval = effect.getTickInterval() != null ? effect.getTickInterval().getSeconds() : 0;

			durationSnapshot.setDuration(duration);
			durationSnapshot.setTickInterval(Duration.seconds(tickInterval));
		}

		return durationSnapshot;
	}

	private Duration getEffectDuration(Duration baseDuration, AccumulatedDurationStats durationStats, AccumulatedTargetStats targetStats) {
		if (baseDuration.isInfinite()) {
			return baseDuration;
		}

		var duration = durationStats.getDuration() + targetStats.getReceivedEffectDuration();
		var durationPct = durationStats.getDurationPct() + targetStats.getReceivedEffectDurationPct();
		var result = addAndMultiplyByPct(baseDuration.getSeconds(), duration, durationPct);

		return Duration.seconds(result);
	}

	@Override
	public DirectSpellDamageSnapshot getDirectSpellDamageSnapshot(Character character, Spell spell, Character target, DirectComponent directComponent, BaseStatsSnapshot baseStats) {
		var spellStats = getAccumulatedDirectComponentStats(character, spell, target, directComponent, baseStats);
		var targetStats = getAccumulatedTargetStats(spell, target, baseStats, PowerType.SPELL_DAMAGE, directComponent.school());

		return getDirectSpellDamageSnapshot(character, spell, target, directComponent, baseStats, spellStats, targetStats);
	}

	private AccumulatedSpellStats getAccumulatedDirectComponentStats(Character character, Spell spell, Character target, DirectComponent directComponent, BaseStatsSnapshot baseStats) {
		var spellStats = newAccumulatedDirectComponentStats(character, spell, target, directComponent);

		accumulateEffects(character, spellStats, baseStats);
		return spellStats;
	}

	private AccumulatedTargetStats getAccumulatedTargetStats(Spell spell, Character target, BaseStatsSnapshot baseStats, PowerType powerType, SpellSchool school) {
		var targetStats = newAccumulatedTargetStats(target, spell, powerType, school);

		accumulateEffects(target, targetStats, baseStats);
		return targetStats;
	}

	@Override
	public DirectSpellDamageSnapshot getDirectSpellDamageSnapshot(Character character, Spell spell, Character target, DirectComponent directComponent, BaseStatsSnapshot baseStats, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		if (directComponent.type() != ComponentType.DAMAGE) {
			throw new IllegalArgumentException();
		}

		var snapshot = new DirectSpellDamageSnapshot(directComponent);

		var critPct = getSpellCritPct(character, spellStats, baseStats, targetStats);
		var critCoeff = getSpellCritCoeff(spellStats);
		var damage = getSpellDamage(spellStats, targetStats);
		var damagePct = getSpellDamagePct(spellStats, targetStats);
		var power = getSpellPower(spellStats, targetStats);
		var powerPct = spellStats.getPowerPct();
		var coeff = getPowerCoefficient(directComponent.coefficient(), spellStats);

		snapshot.setCritPct(critPct);
		snapshot.setCritCoeff(critCoeff);
		snapshot.setDamage(damage);
		snapshot.setDamagePct(damagePct);
		snapshot.setPower(power);
		snapshot.setPowerPct(powerPct);
		snapshot.setCoeff(coeff);

		return snapshot;
	}

	@Override
	public PeriodicSpellDamageSnapshot getPeriodicSpellDamageSnapshot(Character character, Spell spell, Character target, BaseStatsSnapshot baseStats) {
		var effectApplication = spell.getEffectApplication();
		var periodicComponent = effectApplication.effect().getPeriodicComponent();
		var spellStats = getAccumulatedPeriodicComponentStats(character, spell, target, periodicComponent, baseStats);
		var targetStats = getAccumulatedTargetStats(spell, target, baseStats, PowerType.SPELL_DAMAGE, periodicComponent.school());

		return getPeriodicSpellDamageSnapshot(character, spell, target, spellStats, targetStats);
	}

	private AccumulatedSpellStats getAccumulatedPeriodicComponentStats(Character character, Spell spell, Character target, PeriodicComponent periodicComponent, BaseStatsSnapshot baseStats) {
		var spellStats = newAccumulatedPeriodicComponentStats(character, spell, target, periodicComponent);

		accumulateEffects(character, spellStats, baseStats);
		return spellStats;
	}

	@Override
	public PeriodicSpellDamageSnapshot getPeriodicSpellDamageSnapshot(Character character, Spell spell, Character target, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		var effectApplication = spell.getEffectApplication();
		var periodicComponent = effectApplication.effect().getPeriodicComponent();

		if (periodicComponent.type() != ComponentType.DAMAGE) {
			throw new IllegalArgumentException();
		}

		var snapshot = new PeriodicSpellDamageSnapshot(periodicComponent);

		var damage = getSpellDamage(spellStats, targetStats);
		var damagePct = getSpellDamagePct(spellStats, targetStats);
		var power = (int) spellStats.getPower();
		var powerPct = spellStats.getPowerPct();
		var coeff = getPowerCoefficient(periodicComponent.coefficient(), spellStats);

		snapshot.setDamage(damage);
		snapshot.setDamagePct(damagePct);
		snapshot.setPower(power);
		snapshot.setPowerPct(powerPct);
		snapshot.setCoeff(coeff);

		return snapshot;
	}

	@Override
	public StatSummary getStatSummary(Character character) {
		var snapshot = new StatSummary();

		var baseStatsSnapshot = getBaseStatsSnapshot(character);

		var conditionArgs = AttributeConditionArgs.forAnySpell(character);
		var levelDifference = 3;

		var castStats = new AccumulatedCastStats(conditionArgs);
		var hitStats = new AccumulatedHitStats(conditionArgs);
		var spellStats = new AccumulatedSpellStats(conditionArgs);
		var targetStats = new AccumulatedTargetStats(conditionArgs);

		spellStats.accumulateBaseStatInfo(character.getBaseStatInfo());

		accumulateEffects(character, castStats);
		accumulateEffects(character, hitStats);
		accumulateEffects(character, spellStats);

		snapshot.setBaseStatsSnapshot(baseStatsSnapshot);
		snapshot.setSpellPower((int) spellStats.getPower());
		snapshot.setSpellDamage(getSpellDamage(character, conditionArgs));

		snapshot.setSpellDamageBySchool(getSpellDamageBySchool(character));
		snapshot.setSpellHitPctBonus(getSpellHitPctBonus(character, hitStats));
		snapshot.setSpellHitPct(getSpellHitPct(character, hitStats, levelDifference));
		snapshot.setSpellCritPct(getSpellCritPct(character, spellStats, baseStatsSnapshot, targetStats));
		snapshot.setSpellHastePct(getHastePct(character, castStats));
		snapshot.setSpellHitRating((int) hitStats.getHitRating());
		snapshot.setSpellCritRating((int) spellStats.getCritRating());
		snapshot.setSpellHasteRating((int) castStats.getHasteRating());

		return snapshot;
	}

	private Map<SpellSchool, Integer> getSpellDamageBySchool(Character character) {
		return Stream.of(SpellSchool.values()).collect(Collectors.toMap(
				Function.identity(),
				school -> getSpellDamage(character, school)
		));
	}

	private int getSpellDamage(Character character, SpellSchool school) {
		var conditionArgs = AttributeConditionArgs.forAnySpell(character);

		conditionArgs.setSpellSchool(school);
		return getSpellDamage(character, conditionArgs);
	}

	private int getSpellDamage(Character character, AttributeConditionArgs conditionArgs) {
		var spellStats = new AccumulatedSpellStats(conditionArgs);
		conditionArgs.setPowerType(PowerType.SPELL_DAMAGE);
		accumulateEffects(character, spellStats);
		return (int) spellStats.getPower();
	}

	private double getSpellCritPct(Character character, AccumulatedSpellStats spellStats, BaseStatsSnapshot baseStats, AccumulatedTargetStats targetStats) {
		var baseStatInfo = character.getBaseStatInfo();
		var cr = character.getCombatRatingInfo();

		var intellect = baseStats.getIntellect();
		var intCrit = (intellect - baseStatInfo.getBaseIntellect()) / baseStatInfo.getIntellectPerCritPct();
		var ratingCrit = spellStats.getCritRating() / cr.getSpellCrit();
		var pctCrit = spellStats.getCritPct();
		var targetCrit = targetStats.getCritTakenPct();

		var totalCrit = intCrit + ratingCrit + pctCrit + targetCrit;

		return clamp(totalCrit, 0, 100);
	}

	private double getSpellCritCoeff(AccumulatedSpellStats spellStats) {
		var increasedCriticalDamage = spellStats.getCritDamagePct() / 100;
		var talentIncrease = spellStats.getCritDamageMultiplierPct() / 100;
		var extraCritCoeff = spellStats.getCritCoeffPct();

		return 1 + (0.5 + 1.5 * increasedCriticalDamage) * (1 + talentIncrease) + extraCritCoeff;
	}


	private int getSpellDamage(AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		return (int) (spellStats.getDamage() + targetStats.getDamageTaken());
	}

	private double getSpellDamagePct(AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		return spellStats.getDamagePct() + spellStats.getEffectPct() + targetStats.getDamageTakenPct();
	}

	private int getSpellPower(AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		return (int) (spellStats.getPower() + targetStats.getPowerTaken());
	}

	private double getPowerCoefficient(Coefficient coefficient, AccumulatedSpellStats spellStats) {
		var coeff = coefficient.value().value();
		var coeffBonus = spellStats.getPowerCoeffPct();

		return coeff + coeffBonus;
	}

	private void accumulateEffects(Character character, AccumulatedStats stats) {
		accumulateEffects(character, stats, null);
	}

	private void accumulateEffects(Character character, AccumulatedStats stats, BaseStatsSnapshot baseStats) {
		var collector = new DefaultEffectCollector(character, stats);
		collector.solveAll();
		if (baseStats != null) {
			collector.solveStatConversions(baseStats);
		}
	}

	private static class DefaultEffectCollector extends AbstractEffectCollector.OnlyEffects {
		final AccumulatedStats stats;
		List<StatConversion> statConversions;

		DefaultEffectCollector(Character character, AccumulatedStats stats) {
			super(character);
			this.stats = stats;
		}

		@Override
		public void addEffect(Effect effect, int numStacks) {
			if (effect.hasAugmentedAbilities()) {
				return;
			}

			if (effect.hasModifierComponent()) {
				var modifierAttributeList = effect.getModifierAttributeList();
				stats.accumulateAttributes(modifierAttributeList, numStacks);
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

		void solveStatConversions(BaseStatsSnapshot baseStats) {
			if (statConversions != null) {
				stats.solveStatConversions(statConversions, baseStats);
			}
		}
	}

	private static double addAndMultiplyByPct(double value, double modifier, double modifierPct) {
		return max(value + modifier, 0) * max(1 + modifierPct / 100, 0);
	}
}
