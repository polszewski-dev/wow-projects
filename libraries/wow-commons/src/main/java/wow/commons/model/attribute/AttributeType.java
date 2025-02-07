package wow.commons.model.attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

import java.util.Set;

import static wow.commons.model.attribute.ValueType.*;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
@AllArgsConstructor
@Getter
public enum AttributeType {
	STRENGTH("Strength", pointsAndPercents()),
	AGILITY("Agility", pointsAndPercents()),
	STAMINA("Stamina", pointsAndPercents()),
	INTELLECT("Intellect", pointsAndPercents()),
	SPIRIT("Spirit", pointsAndPercents()),

	BASE_STATS("BaseStats", pointsAndPercents()),

	MAX_HEALTH("MaxHealth", pointsAndPercents()),
	MAX_MANA("MaxMana", pointsAndPercents()),

	DAMAGE("Damage", pointsAndPercents()),
	HEALING("Healing", pointsAndPercents()),
	ABSORPTION("Absorption", pointsAndPercents()),
	EFFECT("Effect", percents()),

	POWER("Power", pointsAndPercents()),
	POWER_COEFF("PowerCoeff", percents()),

	HIT("Hit", ratingsAndPercents()),
	CRIT("Crit", ratingsAndPercents()),
	HASTE("Haste", ratingsAndPercents()),

	CRIT_DAMAGE("CritDamage", percents()),//meta
	CRIT_DAMAGE_MULTIPLIER("CritDamageMultiplier", percents()),//talent
	CRIT_COEFF("CritCoeff", percents()),//isb

	DAMAGE_TAKEN("DamageTaken", pointsAndPercents()),
	HEALING_TAKEN("HealingTaken", pointsAndPercents()),
	POWER_TAKEN("PowerTaken", points()),
	CRIT_TAKEN("CritTaken", percents()),

	PENETRATION("Penetration", points()),
	EXPERTISE("Expertise", ratings()),

	MP5("Mp5", points()),
	HP5("Hp5", points()),

	ARMOR("Armor", pointsAndPercents()),
	DODGE("Dodge", ratingsAndPercents()),
	DEFENSE("Defense", pointsRatingsAndPercents()),
	BLOCK("Block", pointsRatingsAndPercents()),
	SHIELD_BLOCK("ShieldBlock", pointsRatingsAndPercents()),
	PARRY("Parry", ratingsAndPercents()),

	RESILIENCE("Resilience", ratingsAndPercents()),
	RESISTANCE("Resistance", pointsAndPercents()),
	EFFECT_RESIST("EffectResist", percents()),

	SPEED("Speed", percents()),

	MANA_COST("ManaCost", pointsAndPercents()),
	ENERGY_COST("EnergyCost", pointsAndPercents()),
	RAGE_COST("RageCost", pointsAndPercents()),
	HEALTH_COST("HealthCost", pointsAndPercents()),
	COST_REDUCTION("CostReduction", percents()),

	CAST_TIME("CastTime", durationAndPercents()),
	DURATION("Duration", durationAndPercents()),
	COOLDOWN("Cooldown", durationAndPercents()),

	RANGE("Range", pointsAndPercents()),
	THREAT("Threat", pointsAndPercents()),
	PUSHBACK("Pushback", percents()),

	RECEIVED_EFFECT_DURATION("ReceivedEffectDuration", pointsAndPercents()),

	HEALTH_GENERATED("HealthGenerated", percents()),
	HEALTH_REGEN("HealthRegen", percents()),
	MANA_REGEN("ManaRegen", percents()),
	IN_COMBAT_HEALTH_REGEN("InCombatHealthRegen", percents()),
	IN_COMBAT_MANA_REGEN("InCombatManaRegen", percents()),

	WEAPON_SKILL("WeaponSkill", points()),
	SKILL("Skill", points()),

	DAMAGE_TAKEN_TRANSFERRED_TO_PET("DamageTakenTransferredToPet", percents()),
	SPELL_REFLECT_PCT("SpellReflect", percents()),
	CHAIN_MULTIPLIER("ChainMultiplier", percents()),

	COPY("Copy", percents())

	;

	private final String key;
	private final Set<ValueType> acceptedValueTypes;

	public static AttributeType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static AttributeType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return key;
	}

	private static Set<ValueType> points() {
		return Set.of(POINT);
	}

	private static Set<ValueType> ratings() {
		return Set.of(RATING);
	}

	private static Set<ValueType> percents() {
		return Set.of(PERCENT);
	}

	private static Set<ValueType> pointsAndPercents() {
		return Set.of(POINT, PERCENT);
	}

	private static Set<ValueType> pointsRatingsAndPercents() {
		return Set.of(POINT, RATING, PERCENT);
	}

	private static Set<ValueType> durationAndPercents() {
		return Set.of(ValueType.DURATION, PERCENT);
	}

	private static Set<ValueType> ratingsAndPercents() {
		return Set.of(RATING, PERCENT);
	}
}
