package wow.commons.model.attribute.primitive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.attribute.AttributeId;
import wow.commons.util.EnumUtil;

import java.util.Set;

import static wow.commons.model.attribute.primitive.ValueType.*;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
@AllArgsConstructor
@Getter
public enum PrimitiveAttributeType implements AttributeId {
	STRENGTH("Strength", pointsAndPercents(), petsAllowed()),
	AGILITY("Agility", pointsAndPercents(), petsAllowed()),
	STAMINA("Stamina", pointsAndPercents(), petsAllowed()),
	INTELLECT("Intellect", pointsAndPercents(), petsAllowed()),
	SPIRIT("Spirit", pointsAndPercents(), petsAllowed()),

	BASE_STATS("BaseStats", pointsAndPercents()),

	POWER("Power", allPowerTypes(), pointsAndPercents()),
	HIT("Hit", allPowerTypes(), ratingsAndPercents()),
	CRIT("Crit", allPowerTypes(), ratingsAndPercents(), petsAllowed()),
	HASTE("Haste", allPowerTypes(), ratingsAndPercents()),

	DAMAGE("Damage", allPowerTypes(), allDamageTypes(), percents(), petsAllowed()),

	CRIT_DAMAGE("CritDamage", allPowerTypes(), percents()),//meta
	CRIT_DAMAGE_MULTIPLIER("CritDamageMultiplier", percents()),//talent
	CRIT_COEFF("CritCoeff", allPowerTypes(), percents()),//isb

	EFFECT("Effect", percents()),
	POWER_COEFF("PowerCoeff", percents()),

	PENETRATION("Penetration", allPowerTypes(), points()),
	EXPERTISE("Expertise", ratingsAndPercents()),

	MP5("Mp5", points()),
	HP5("Hp5", points()),

	ARMOR("Armor", points()),
	DODGE("Dodge", ratingsAndPercents()),
	DEFENSE("Defense", pointsRatingsAndPercents()),
	BLOCK("Block", pointsRatingsAndPercents()),
	SHIELD_BLOCK("ShieldBlock", pointsRatingsAndPercents()),
	PARRY("Parry", ratingsAndPercents()),

	RESILIENCE("Resilience", ratingsAndPercents()),
	RESISTANCE("Resistance", points()),

	SPEED("Speed", percents()),

	COST("Cost", percents()),
	COST_REDUCTION_PCT("CostReduction", percents()),
	CAST_TIME("CastTime", durationAndPercents()),
	DURATION("Duration", durationAndPercents()),
	COOLDOWN("Cooldown", durationAndPercents()),
	THREAT("Threat", percents()),
	PUSHBACK("Pushback", percents()),
	RANGE("Range", percents()),

	MAX_HEALTH("MaxHealth", pointsAndPercents(), petsAllowed()),
	MAX_MANA("MaxMana", pointsAndPercents(), petsAllowed()),

	MANA_TRANSFERRED_TO_PET("ManaTransferredToPet", percents()),
	NUM_NEXT_SPELLS_CAST_INSTANTLY("NumNextSpellsCastInstantly", points());

	private final String key;
	private final Set<PowerType> acceptedPowerTypes;
	private final Set<DamageType> acceptedDamageTypes;
	private final Set<ValueType> acceptedValueTypes;
	private final boolean petsAllowed;

	PrimitiveAttributeType(String key, Set<ValueType> acceptedValueTypes) {
		this(key, Set.of(PowerType.ANY), Set.of(DamageType.ANY), acceptedValueTypes, false);
	}

	PrimitiveAttributeType(String key, Set<PowerType> acceptedPowerTypes, Set<ValueType> acceptedValueTypes) {
		this(key, acceptedPowerTypes, Set.of(DamageType.ANY), acceptedValueTypes, false);
	}

	PrimitiveAttributeType(String key, Set<ValueType> acceptedValueTypes, boolean petsAllowed) {
		this(key, Set.of(PowerType.ANY), Set.of(DamageType.ANY), acceptedValueTypes, petsAllowed);
	}

	PrimitiveAttributeType(String key, Set<PowerType> acceptedPowerTypes, Set<ValueType> acceptedValueTypes, boolean petsAllowed) {
		this(key, acceptedPowerTypes, Set.of(DamageType.ANY), acceptedValueTypes, petsAllowed);
	}

	public static PrimitiveAttributeType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static PrimitiveAttributeType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	public String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return key;
	}

	private static Set<PowerType> allPowerTypes() {
		return Set.of(PowerType.values());
	}

	private static Set<DamageType> allDamageTypes() {
		return Set.of(DamageType.values());
	}

	private static Set<ValueType> points() {
		return Set.of(POINT);
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

	private static boolean petsAllowed() {
		return true;
	}
}
