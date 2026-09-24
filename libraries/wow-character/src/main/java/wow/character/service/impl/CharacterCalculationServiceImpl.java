package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.character.PetCharacter;
import wow.character.model.snapshot.*;
import wow.character.service.CharacterCalculationService;
import wow.character.util.AbstractEffectCollector;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectAugmentations;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.component.StatConversion;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.Coefficient;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Math.*;
import static wow.character.model.snapshot.AttributePredicates.*;
import static wow.character.util.StatConversionConditionChecker.check;
import static wow.commons.constant.SpellConstants.*;
import static wow.commons.model.attribute.AttributeId.COPY_PCT;
import static wow.commons.model.attribute.AttributeId.EFFECT_PCT;
import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;
import static wow.commons.model.spell.SpellSchool.magicSpellSchoolsStream;
import static wow.commons.model.spell.SpellTargetType.GROUND;
import static wow.commons.model.spell.component.ComponentCommand.*;

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

		return new AccumulatedBaseStats(conditionArgs);
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
		var conditionArgs = AttributeConditionArgs.forSpellTarget(target, spell, powerType, school);

		return new AccumulatedTargetStats(conditionArgs);
	}

	@Override
	public AccumulatedHitStats newAccumulatedHitStats(Character character, Spell spell, Character target) {
		var conditionArgs = AttributeConditionArgs.forSpellDamage(character, spell, target, null);

		return new AccumulatedHitStats(conditionArgs);
	}

	@Override
	public AccumulatedDurationStats newAccumulatedDurationStats(Character character, Spell spell, Character target, ApplyEffect command) {
		var conditionArgs = AttributeConditionArgs.forSpell(character, spell, target);

		conditionArgs.setAppliedEffect(command.effect());

		return new AccumulatedDurationStats(conditionArgs);
	}

	@Override
	public AccumulatedReceivedEffectStats newAccumulatedReceivedEffectStats(Character target, Spell spell, ApplyEffect command) {
		var conditionArgs = AttributeConditionArgs.forSpellTarget(target, spell);

		conditionArgs.setAppliedEffect(command.effect());

		return new AccumulatedReceivedEffectStats(conditionArgs);
	}

	@Override
	public AccumulatedSpellStats newAccumulatedDirectComponentStats(Character character, Spell spell, Character target, PowerType powerType, ChangeHealthDirectly command) {
		var conditionArgs = AttributeConditionArgs.forSpell(character, spell, target, powerType, command.school());

		conditionArgs.setDirect(true);

		return newAccumulatedSpellStats(conditionArgs);
	}

	@Override
	public AccumulatedSpellStats newAccumulatedPeriodicComponentStats(Character character, Spell spell, Character target, PowerType powerType, PeriodicCommand command) {
		var conditionArgs = AttributeConditionArgs.forSpell(character, spell, target, powerType, command.school());

		conditionArgs.setPeriodic(true);

		return newAccumulatedSpellStats(conditionArgs);
	}

	private AccumulatedSpellStats newAccumulatedSpellStats(AttributeConditionArgs conditionArgs) {
		return new AccumulatedSpellStats(conditionArgs);
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

		var strength = (int) stats.getTotalStrength();
		var agility = (int) stats.getTotalAgility();
		var stamina = (int) stats.getTotalStamina();
		var intellect = (int) stats.getTotalIntellect();
		var spirit = (int) stats.getTotalSpirit();
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
	public SpellCostSnapshot getSpellCostSnapshot(Character character, Ability ability, Character target) {
		if (ability.getCost() == null) {
			return null;
		}

		var costStats = getAccumulatedCostStats(character, ability, target);

		return getSpellCostSnapshot(character, ability, costStats);
	}

	private AccumulatedCostStats getAccumulatedCostStats(Character character, Ability ability, Character target) {
		var costStats = newAccumulatedCostStats(character, ability, target);

		accumulateEffects(character, costStats);
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
	public EffectDurationSnapshot getEffectDurationSnapshot(Character character, Spell spell, Character target, ApplyEffect command) {
		var durationStats = getAccumulatedDurationStats(character, spell, target, command);
		var receivedEffectStats = getAccumulatedReceivedEffectStats(spell, target, command);

		return getEffectDurationSnapshot(character, spell, command, durationStats, receivedEffectStats);
	}

	private AccumulatedDurationStats getAccumulatedDurationStats(Character character, Spell spell, Character target, ApplyEffect command) {
		var durationStats = newAccumulatedDurationStats(character, spell, target, command);

		accumulateEffects(character, durationStats);
		return durationStats;
	}

	private AccumulatedReceivedEffectStats getAccumulatedReceivedEffectStats(Spell spell, Character target, ApplyEffect command) {
		if (spell instanceof Ability ability && (ability.isChanneled() || command.target().hasType(GROUND))) {
			return null;
		}

		var receivedEffectStats = newAccumulatedReceivedEffectStats(target, spell, command);

		accumulateEffects(target, receivedEffectStats);
		return receivedEffectStats;
	}

	@Override
	public EffectDurationSnapshot getEffectDurationSnapshot(Character character, Spell spell, ApplyEffect command, AccumulatedDurationStats durationStats, AccumulatedReceivedEffectStats receivedEffectStats) {
		if (spell instanceof Ability ability && ability.isChanneled()) {
			return getChanneledEffectDurationSnapshot(character, command, durationStats, receivedEffectStats);
		}

		var tickInterval = command.effect().getTickInterval();

		if (tickInterval != null) {
			return getPeriodicEffectDurationSnapshot(command, durationStats, receivedEffectStats);
		} else {
			return getNonPeriodicEffectDurationSnapshot(command, durationStats, receivedEffectStats);
		}
	}

	private EffectDurationSnapshot getChanneledEffectDurationSnapshot(Character character, ApplyEffect command, AccumulatedDurationStats durationStats, AccumulatedReceivedEffectStats receivedEffectStats) {
		var effect = command.effect();

		var baseDuration = (Duration) command.duration();
		var baseTickIntervalMillis = effect.getTickInterval().millis();
		var duration = getEffectDuration(baseDuration, durationStats, receivedEffectStats);
		var durationMillis = duration.millis();
		var numTicks = durationMillis / baseTickIntervalMillis;
		var numTicksNotRounded = (double) durationMillis / baseTickIntervalMillis;

		var hastePct = getHastePct(character, durationStats);
		var tickIntervalMillis = (long) getActualCastTime(baseTickIntervalMillis, hastePct);
		var channelTimeMillis = (long) (tickIntervalMillis * numTicksNotRounded);

		var durationSnapshot = new EffectDurationSnapshot();

		durationSnapshot.setNumTicks((int) numTicks);
		durationSnapshot.setTickInterval(Duration.millis(tickIntervalMillis));
		durationSnapshot.setDuration(Duration.millis(channelTimeMillis));

		return durationSnapshot;
	}

	private EffectDurationSnapshot getPeriodicEffectDurationSnapshot(ApplyEffect command, AccumulatedDurationStats durationStats, AccumulatedReceivedEffectStats receivedEffectStats) {
		var effect = command.effect();
		var baseDuration = (Duration) command.duration();
		var tickInterval = effect.getTickInterval();
		var duration = getEffectDuration(baseDuration, durationStats, receivedEffectStats);
		var numTicks = duration.millis() / tickInterval.millis();

		var durationSnapshot = new EffectDurationSnapshot();

		durationSnapshot.setNumTicks((int) numTicks);
		durationSnapshot.setTickInterval(tickInterval);
		durationSnapshot.setDuration(duration);

		return durationSnapshot;
	}

	private EffectDurationSnapshot getNonPeriodicEffectDurationSnapshot(ApplyEffect command, AccumulatedDurationStats durationStats, AccumulatedReceivedEffectStats receivedEffectStats) {
		var baseDuration = command.duration();
		var duration = baseDuration.isInfinite()
				? baseDuration
				: getEffectDuration((Duration) baseDuration, durationStats, receivedEffectStats);

		var durationSnapshot = new EffectDurationSnapshot();

		durationSnapshot.setNumTicks(0);
		durationSnapshot.setTickInterval(Duration.ZERO);
		durationSnapshot.setDuration(duration);

		return durationSnapshot;
	}

	private Duration getEffectDuration(Duration baseDuration, AccumulatedDurationStats durationStats, AccumulatedReceivedEffectStats receivedEffectStats) {
		var baseDurationSeconds = baseDuration.getSeconds();
		var duration = durationStats.getDuration();
		var durationPct = durationStats.getDurationPct();

		if (receivedEffectStats != null) {
			duration += receivedEffectStats.getReceivedEffectDuration();
			durationPct += receivedEffectStats.getReceivedEffectDurationPct();
		}

		var result = addAndMultiplyByPct(baseDurationSeconds, duration, durationPct);

		return Duration.seconds(result);
	}

	@Override
	public AnyDuration getSummonDuration(Character character, Spell spell, AnyDuration baseDuration) {
		if (baseDuration.isInfinite()) {
			return baseDuration;
		}

		var conditionArgs = AttributeConditionArgs.forSpell(character, spell, null);
		var durationStats = new AccumulatedDurationStats(conditionArgs);

		accumulateEffects(character, durationStats);

		var baseDurationSeconds = ((Duration) baseDuration).getSeconds();
		var duration = durationStats.getDuration();
		var durationPct = durationStats.getDurationPct();

		var result = addAndMultiplyByPct(baseDurationSeconds, duration, durationPct);

		return Duration.seconds(result);
	}

	@Override
	public DirectSpellComponentSnapshot getDirectSpellDamageSnapshot(Character character, Spell spell, Character target, DealDamageDirectly command) {
		return getDirectSpellComponentSnapshot(character, spell, target, command, SPELL_DAMAGE);
	}

	@Override
	public DirectSpellComponentSnapshot getDirectHealingSnapshot(Character character, Spell spell, Character target, HealDirectly command) {
		return getDirectSpellComponentSnapshot(character, spell, target, command, HEALING);
	}

	private DirectSpellComponentSnapshot getDirectSpellComponentSnapshot(Character character, Spell spell, Character target, ChangeHealthDirectly command, PowerType powerType) {
		var spellStats = getAccumulatedDirectComponentStats(character, spell, target, powerType, command);
		var targetStats = getAccumulatedTargetStats(spell, target, powerType, command.school());

		return getDirectSpellComponentSnapshot(character, spell, target, command, spellStats, targetStats);
	}

	private AccumulatedSpellStats getAccumulatedDirectComponentStats(Character character, Spell spell, Character target, PowerType powerType, ChangeHealthDirectly command) {
		var spellStats = newAccumulatedDirectComponentStats(character, spell, target, powerType, command);

		accumulateEffects(character, spellStats);
		return spellStats;
	}

	private AccumulatedTargetStats getAccumulatedTargetStats(Spell spell, Character target, PowerType powerType, SpellSchool school) {
		var targetStats = newAccumulatedTargetStats(target, spell, powerType, school);

		accumulateEffects(target, targetStats);
		return targetStats;
	}

	@Override
	public DirectSpellComponentSnapshot getDirectSpellComponentSnapshot(Character character, Spell spell, Character target, ChangeHealthDirectly command, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		var snapshot = new DirectSpellComponentSnapshot(command);

		var critPct = getSpellCritPct(character, spellStats, targetStats);
		var critCoeff = getSpellCritCoeff(spellStats);
		var amount = getSpellAmount(spellStats, targetStats);
		var amountPct = getSpellAmountPct(spellStats, targetStats);
		var power = getSpellPower(spellStats, targetStats);
		var powerPct = spellStats.getPowerPct();
		var coeff = getPowerCoefficient(command.coefficient(), spellStats);

		snapshot.setCritPct(critPct);
		snapshot.setCritCoeff(critCoeff);
		snapshot.setAmount(amount);
		snapshot.setAmountPct(amountPct);
		snapshot.setPower(power);
		snapshot.setPowerPct(powerPct);
		snapshot.setCoeff(coeff);

		return snapshot;
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicSpellDamageSnapshot(Character character, Spell spell, Character target, DealDamagePeriodically command) {
		return getPeriodicComponentSnapshot(character, spell, target, command, SPELL_DAMAGE);
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicHealingSnapshot(Character character, Spell spell, Character target, HealPeriodically command) {
		return getPeriodicComponentSnapshot(character, spell, target, command, HEALING);
	}

	private PeriodicSpellComponentSnapshot getPeriodicComponentSnapshot(Character character, Spell spell, Character target, ChangeHealthPeriodically command, PowerType powerType) {
		var spellStats = getAccumulatedPeriodicComponentStats(character, spell, target, powerType, command);
		var targetStats = getAccumulatedTargetStats(spell, target, powerType, command.school());

		return getPeriodicComponentSnapshot(character, spell, target, command, spellStats, targetStats);
	}

	private AccumulatedSpellStats getAccumulatedPeriodicComponentStats(Character character, Spell spell, Character target, PowerType powerType, PeriodicCommand command) {
		var spellStats = newAccumulatedPeriodicComponentStats(character, spell, target, powerType, command);

		accumulateEffects(character, spellStats);
		return spellStats;
	}

	@Override
	public PeriodicSpellComponentSnapshot getPeriodicComponentSnapshot(Character character, Spell spell, Character target, ChangeHealthPeriodically command, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		var snapshot = new PeriodicSpellComponentSnapshot(command);

		var amount = getSpellAmount(spellStats, targetStats);
		var amountPct = getSpellAmountPct(spellStats, targetStats);
		var power = (int) spellStats.getPower();
		var powerPct = spellStats.getPowerPct();
		var coeff = getPowerCoefficient(command.coefficient(), spellStats);

		snapshot.setAmount(amount);
		snapshot.setAmountPct(amountPct);
		snapshot.setPower(power);
		snapshot.setPowerPct(powerPct);
		snapshot.setCoeff(coeff);

		return snapshot;
	}

	@Override
	public RegenSnapshot getRegenSnapshot(Character character) {
		var baseStats = getBaseStatsSnapshot(character);
		var regenStats = getRegenStats(character);

		var spiritBasedRegen = getSpiritBasedRegen(baseStats, character);
		var manaRegenPct = regenStats.getManaRegenPct();
		var mp5 = regenStats.getMp5();
		var inCombatManaRegenPct = clamp(regenStats.getInCombatManaRegenPct(), 0, 100);

		var uninterruptedManaRegen = (spiritBasedRegen + mp5) * (1 + manaRegenPct / 100);
		var interruptedManaRegen = (spiritBasedRegen * inCombatManaRegenPct / 100 + mp5) * (1 + manaRegenPct / 100);

		var baseHealthRegen = 0;
		var healthRegenPct = regenStats.getHealthRegenPct();
		var hp5 = regenStats.getHp5();
		var inCombatHealthRegenPct = clamp(regenStats.getInCombatHealthRegenPct(), 0, 100);

		var outOfCombatHealthRegen = (baseHealthRegen + hp5) * (1 + healthRegenPct / 100);
		var inCombatHealthRegen = (baseHealthRegen * inCombatHealthRegenPct / 100 + hp5) * (1 + healthRegenPct / 100);

		var snapshot = new RegenSnapshot();

		snapshot.setUninterruptedManaRegen((int) uninterruptedManaRegen);
		snapshot.setInterruptedManaRegen((int) interruptedManaRegen);
		snapshot.setOutOfCombatHealthRegen((int) outOfCombatHealthRegen);
		snapshot.setInCombatHealthRegen((int) inCombatHealthRegen);
		snapshot.setHealthGeneratedPct(regenStats.getHealingTakenPct());

		return snapshot;
	}

	private int getSpiritBasedRegen(BaseStatsSnapshot baseStats, Character character) {
		var intellect = baseStats.getIntellect();
		var spirit = baseStats.getSpirit();
		var baseRegen = getBaseManaRegen(character);

		return (int) ceil(5 * (0.001 + sqrt(intellect) * spirit * baseRegen));
	}

	private double getBaseManaRegen(Character character) {
		int level = character.getLevel();
		int maxLevel = BASE_MANA_REGEN_PER_LEVEL.length - 1;

		if (level > maxLevel) {
			return BASE_MANA_REGEN_PER_LEVEL[maxLevel];
		}

		return BASE_MANA_REGEN_PER_LEVEL[level];
	}

	private AccumulatedRegenStats getRegenStats(Character character) {
		var conditionArgs = AttributeConditionArgs.forRegen(character);
		var regenStats = new AccumulatedRegenStats(conditionArgs);

		accumulateEffects(character, regenStats);

		return regenStats;
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

		accumulateEffects(character, castStats);
		accumulateEffects(character, hitStats);
		accumulateEffects(character, spellStats);

		var regenSnapshot = getRegenSnapshot(character);

		snapshot.setBaseStatsSnapshot(baseStatsSnapshot);
		snapshot.setSpellPower((int) spellStats.getPower());
		snapshot.setSpellDamage(getSpellDamage(character));
		snapshot.setSpellDamageBySchool(getSpellDamageBySchool(character));
		snapshot.setSpellDamagePctBySchool(getSpellDamagePctBySchool(character));
		snapshot.setSpellHealing(getSpellHealing(character));
		snapshot.setSpellHitPctBonus(getSpellHitPctBonus(character, hitStats));
		snapshot.setSpellHitPct(getSpellHitPct(character, hitStats, levelDifference));
		snapshot.setSpellCritPct(getSpellCritPct(character, spellStats, targetStats));
		snapshot.setSpellHastePct(getHastePct(character, castStats));
		snapshot.setSpellHitRating((int) hitStats.getHitRating());
		snapshot.setSpellCritRating((int) spellStats.getCritRating());
		snapshot.setSpellHasteRating((int) castStats.getHasteRating());
		snapshot.setOutOfCombatHealthRegen(regenSnapshot.getOutOfCombatHealthRegen());
		snapshot.setInCombatHealthRegen(regenSnapshot.getInCombatHealthRegen());
		snapshot.setUninterruptedManaRegen(regenSnapshot.getUninterruptedManaRegen());
		snapshot.setInterruptedManaRegen(regenSnapshot.getInterruptedManaRegen());

		return snapshot;
	}

	@Override
	public EffectAugmentations getEffectAugmentations(Character character, Spell spell, Character target) {
		if (!(spell instanceof Ability ability)) {
			return EffectAugmentations.EMPTY;
		}

		var collector = new EffectAugmentationCollector(character, ability, target);

		collector.solveAll();

		return new EffectAugmentations(
			collector.accumulatedEffectIncreasePct.totalValue,
			collector.extraModifiers,
			collector.extraStatConversions,
			collector.extraEvents
		);
	}

	@Override
	public double getCopiedAmountAsDamage(Character character, Spell spell, Character target, int amount, double ratioPct) {
		var copyIncreasePct = getCopiedAmountIncreasePct(character, spell);
		var targetStats = getAccumulatedTargetStats(spell, target, SPELL_DAMAGE, spell.getSchool());
		var damageTaken = targetStats.getAmountTaken();
		var damageTakenPct = targetStats.getAmountTakenPct();

		return multiplyByRatio(amount + damageTaken, ratioPct + copyIncreasePct, damageTakenPct);
	}

	@Override
	public double getCopiedAmountAsHeal(Character character, Spell spell, Character target, int amount, double ratioPct) {
		var copyIncreasePct = getCopiedAmountIncreasePct(character, spell);
		var targetStats = getAccumulatedTargetStats(spell, target, HEALING, spell.getSchool());
		var healingTaken = targetStats.getAmountTaken();
		var healingTakenPct = targetStats.getAmountTakenPct();

		return multiplyByRatio(amount + healingTaken, ratioPct + copyIncreasePct, healingTakenPct);
	}

	@Override
	public double getCopiedAmountAsManaGain(Character character, Spell spell, Character target, int amount, double ratioPct) {
		var copyIncreasePct = getCopiedAmountIncreasePct(character, spell);

		return multiplyByRatio(amount, ratioPct + copyIncreasePct, 0);
	}

	private double getCopiedAmountIncreasePct(Character character, Spell spell) {
		if (!(spell instanceof Ability ability)) {
			return 0;
		}

		var args = AttributeConditionArgs.forSpell(character, ability, null);
		var accumulatedCopyPct = new AccumulatedSingleStat(COPY_PCT, args);

		accumulateEffects(character, accumulatedCopyPct);

		return accumulatedCopyPct.totalValue;
	}

	private static double multiplyByRatio(double value, double ratioPct, double increasePct) {
		return value * max(ratioPct / 100.0, 0) * max(1 + increasePct / 100.0, 0);
	}

	private Map<SpellSchool, Integer> getSpellDamageBySchool(Character character) {
		return magicSpellSchoolsStream().collect(Collectors.toMap(
				Function.identity(),
				school -> getSpellDamage(character, school)
		));
	}

	private Map<SpellSchool, Double> getSpellDamagePctBySchool(Character character) {
		return magicSpellSchoolsStream().collect(Collectors.toMap(
				Function.identity(),
				school -> getSpellDamagePct(character, school)
		));
	}

	private double getSpellDamagePct(Character character, SpellSchool school) {
		return getSpellPctAmount(character, SPELL_DAMAGE, school);
	}

	private double getSpellPctAmount(Character character, PowerType powerType, SpellSchool school) {
		var conditionArgs = AttributeConditionArgs.forAnySpell(character, powerType, school);
		var spellStats = new AccumulatedSpellStats(conditionArgs);

		accumulateEffects(character, spellStats);
		return spellStats.getAmountPct();
	}

	private int getSpellDamage(Character character) {
		return getSpellDamage(character, null);
	}

	private int getSpellDamage(Character character, SpellSchool school) {
		return getSpellAmount(character, SPELL_DAMAGE, school);
	}

	private int getSpellHealing(Character character) {
		return getSpellAmount(character, HEALING, null);
	}

	private int getSpellAmount(Character character, PowerType powerType, SpellSchool school) {
		var conditionArgs = AttributeConditionArgs.forAnySpell(character, powerType, school);
		var spellStats = new AccumulatedSpellStats(conditionArgs);

		accumulateEffects(character, spellStats);
		return (int) spellStats.getPower();
	}

	private double getSpellCritPct(Character character, AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		var baseStatInfo = character.getBaseStatInfo();
		var cr = character.getCombatRatingInfo();

		var intellect = (int) spellStats.getTotalIntellect();
		var intCrit = intellect / baseStatInfo.getIntellectPerCritPct();
		var ratingCrit = spellStats.getCritRating() / cr.getSpellCrit();
		var pctCrit = spellStats.getCritPct();
		var targetCrit = targetStats.getCritTakenPct();

		var totalCrit = intCrit + ratingCrit + pctCrit + targetCrit;

		return clamp(totalCrit, 0, 100);
	}

	private double getSpellCritCoeff(AccumulatedSpellStats spellStats) {
		var increasedCriticalEffect = spellStats.getCritEffectPct() / 100;
		var talentIncrease = spellStats.getCritEffectMultiplierPct() / 100;
		var extraCritCoeff = spellStats.getCritCoeffPct();

		return 1 + (0.5 + 1.5 * increasedCriticalEffect) * (1 + talentIncrease) + extraCritCoeff;
	}


	private int getSpellAmount(AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		return (int) (spellStats.getAmount() + targetStats.getAmountTaken());
	}

	private double getSpellAmountPct(AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		return spellStats.getAmountPct() + spellStats.getEffectPct() + targetStats.getAmountTakenPct();
	}

	private int getSpellPower(AccumulatedSpellStats spellStats, AccumulatedTargetStats targetStats) {
		return (int) (spellStats.getPower() + targetStats.getPowerTaken());
	}

	private double getPowerCoefficient(Coefficient coefficient, AccumulatedSpellStats spellStats) {
		var coeff = coefficient.value().value();
		var coeffBonus = spellStats.getPowerCoeffPct();

		return coeff + coeffBonus;
	}

	private void accumulateEffects(Character character, AccumulatedPartialStats stats) {
		accumulateEffects(character, stats, true);
	}

	private DefaultEffectCollector accumulateEffects(Character character, AccumulatedPartialStats stats, boolean performConversions) {
		var collector = new DefaultEffectCollector(character, OWNER_OR_AURAS, stats);

		collector.solveAll();

		if (character instanceof PetCharacter petCharacter) {
			var masterCollector = new DefaultEffectCollector(petCharacter.getMaster(), PET_ONLY, stats);

			masterCollector.solveAll();
		}

		if (performConversions) {
			collector.solveStatConversions(this);
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

		void solveStatConversions(CharacterCalculationServiceImpl service) {
			var solver = newStatsConversionSolver(service);

			if (solver != null) {
				solver.solve();
			}
		}

		StatConversionSolver newStatsConversionSolver(CharacterCalculationServiceImpl service) {
			if (statConversions == null) {
				return null;
			}

			return service.new StatConversionSolver(character, stats, statConversions);
		}
	}

	private class StatConversionSolver {
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

			return collector.newStatsConversionSolver(CharacterCalculationServiceImpl.this);
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

	private static double addAndMultiplyByPct(double value, double modifier, double modifierPct) {
		return max(value + modifier, 0) * max(1 + modifierPct / 100, 0);
	}

	private static class AccumulatedSingleStat extends AccumulatedPartialStats {
		private final AttributeId attributeId;
		private double totalValue;

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

	private static class EffectAugmentationCollector extends AbstractEffectCollector.OnlyEffects {
		final Ability ability;
		final Character target;
		final AccumulatedSingleStat accumulatedEffectIncreasePct;

		List<Attribute> extraModifiers = new ArrayList<>();
		List<StatConversion> extraStatConversions = new ArrayList<>();
		List<Event> extraEvents = new ArrayList<>();

		EffectAugmentationCollector(Character character, Ability ability, Character target) {
			super(character);
			this.ability = ability;
			this.target = target;
			var args = AttributeConditionArgs.forSpell(character, ability, null);
			this.accumulatedEffectIncreasePct = new AccumulatedSingleStat(EFFECT_PCT, args);
		}

		@Override
		public void addEffect(Effect effect, int stackCount) {
			if (!effect.hasAugmentedAbilities()) {
				checkForMatchingEffectIncreases(effect, stackCount);
			} else if (effect.augments(ability.getAbilityId())) {
				extractAugmentations(effect);
			}
		}

		private void extractAugmentations(Effect effect) {
			var modifierAttributeList = effect.getModifierAttributeList();

			if (modifierAttributeList != null) {
				extraModifiers.addAll(modifierAttributeList);
			}

			extraStatConversions.addAll(effect.getStatConversions());
			extraEvents.addAll(effect.getEvents());
		}

		private void checkForMatchingEffectIncreases(Effect effect, int stackCount) {
			var modifierAttributeList = effect.getModifierAttributeList();

			if (modifierAttributeList != null) {
				accumulatedEffectIncreasePct.accumulateAttributes(modifierAttributeList, stackCount, ANY_TARGET);
			}
		}
	}

	private static final double[] BASE_MANA_REGEN_PER_LEVEL = {
			0,
			0.034965,
			0.034191,
			0.033465,
			0.032526,
			0.031661,
			0.031076,
			0.030523,
			0.029994,
			0.029307,
			0.028661,
			0.027584,
			0.026215,
			0.025381,
			0.024300,
			0.023345,
			0.022748,
			0.021958,
			0.021386,
			0.020790,
			0.020121,
			0.019733,
			0.019155,
			0.018819,
			0.018316,
			0.017936,
			0.017576,
			0.017201,
			0.016919,
			0.016581,
			0.016233,
			0.015994,
			0.015707,
			0.015464,
			0.015204,
			0.014956,
			0.014744,
			0.014495,
			0.014302,
			0.014094,
			0.013895,
			0.013724,
			0.013522,
			0.013363,
			0.013175,
			0.012996,
			0.012853,
			0.012687,
			0.012539,
			0.012384,
			0.012233,
			0.012113,
			0.011973,
			0.011859,
			0.011714,
			0.011575,
			0.011473,
			0.011342,
			0.011245,
			0.011110,
			0.010999,
			0.010700,
			0.010522,
			0.010290,
			0.010119,
			0.009968,
			0.009808,
			0.009651,
			0.009553,
			0.009445,
			0.009327,
	};
}
