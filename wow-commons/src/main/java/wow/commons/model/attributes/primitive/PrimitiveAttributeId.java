package wow.commons.model.attributes.primitive;

import lombok.Getter;
import wow.commons.model.attributes.AttributeId;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
@Getter
public enum PrimitiveAttributeId implements AttributeId {
	STRENGTH("Strength"),
	STRENGTH_PCT("Strength,Percent"),
	AGILITY("Agility"),
	AGILITY_PCT("Agility,Percent"),
	STAMINA("Stamina"),
	STAMINA_PCT("Stamina,Percent"),
	INTELLECT("Intellect"),
	INTELLECT_PCT("Intellect,Percent"),
	SPIRIT("Spirit"),
	SPIRIT_PCT("Spirit,Percent"),
	BASE_STATS("BaseStats"),
	BASE_STATS_PCT("BaseStats,Percent"),
	ATTACK_POWER("Power,Physical"),
	RANGED_ATTACK_POWER("Power,Ranged"),
	SPELL_POWER("Power,Spell"),
	SPELL_DAMAGE("Power,SpellDamage"),
	SPELL_DAMAGE_PCT("Power,SpellDamage,Percent"),
	HEALING_POWER("Power,Healing"),
	HIT_RATING("Hit,Rating"),
	HIT_PCT("Hit,Percent"),
	PHYSICAL_HIT_RATING("Hit,Physical,Rating"),
	PHYSICAL_HIT_PCT("Hit,Physical,Percent"),
	RANGED_HIT_PCT("Hit,Ranged,Percent"),
	SPELL_HIT_RATING("Hit,Spell,Rating"),
	SPELL_HIT_PCT("Hit,Spell,Percent"),
	CRIT_RATING("Crit,Rating"),
	CRIT_PCT("Crit,Percent"),
	PHYSICAL_CRIT_RATING("Crit,Physical,Rating"),
	PHYSICAL_CRIT_PCT("Crit,Physical,Percent"),
	SPELL_CRIT_RATING("Crit,Spell,Rating"),
	SPELL_CRIT_PCT("Crit,Spell,Percent"),
	HASTE_RATING("Haste,Rating"),
	HASTE_PCT("Haste,Percent"),
	PHYSICAL_HASTE_RATING("Haste,Physical,Rating"),
	PHYSICAL_HASTE_PCT("Haste,Physical,Percent"),
	SPELL_HASTE_RATING("Haste,Spell,Rating"),
	SPELL_HASTE_PCT("Haste,Spell,Percent"),
	DAMAGE_PCT("Damage,Percent"),
	DIRECT_DAMAGE_PCT("Damage,Direct,Percent"),
	DOT_DAMAGE_PCT("Damage,DoT,Percent"),
	OFFENSIVE_SPELL_DAMAGE_PCT("Damage,SpellDamage,Percent"),
	OFFENSIVE_SPELL_CRIT_PCT("Crit,SpellDamage,Percent"),
	CRIT_DAMAGE_PCT("CritDamage,Percent"),
	CRIT_DAMAGE_MULTIPLIER_PCT("CritDamageMultiplier,Percent"),
	CRIT_COEFF_PCT("CritCoeff,Percent"),
	EFFECT_PCT("Effect,Percent"),
	POWER_COEFFICIENT_PCT("PowerCoeff,Percent"),
	ARMOR_PENETRATION("Penetration,Physical"),
	SPELL_PENETRATION("Penetration,Spell"),
	EXPERTISE_RATING("Expertise,Rating"),
	WEAPON_DAMAGE("Power,Weapon"),
	WEAPON_HIT_RATING("Hit,Weapon,Rating"),
	WEAPON_HIT_PCT("Hit,Weapon,Percent"),
	WEAPON_CRIT_RATING("Crit,Weapon,Rating"),
	WEAPON_HASTE_RATING("Haste,Weapon,Rating"),
	WEAPON_HASTE_PCT("Haste,Weapon,Percent"),
	MP5("Mp5"),
	HP5("Hp5"),
	ARMOR("Armor"),
	DODGE_RATING("Dodge,Rating"),
	DODGE_PCT("Dodge,Percent"),
	DEFENSE("Defense"),
	DEFENSE_RATING("Defense,Rating"),
	BLOCK("Block"),
	BLOCK_RATING("Block,Rating"),
	BLOCK_PCT("Block,Percent"),
	SHIELD_BLOCK("ShieldBlock"),
	SHIELD_BLOCK_RATING("ShieldBlock,Rating"),
	SHIELD_BLOCK_PCT("ShieldBlock,Percent"),
	PARRY_RATING("Parry,Rating"),
	RESILIENCE_RATING("Resilience,Rating"),
	RESISTANCE("Resistance"),
	SPEEED_PCT("Speed,Percent"),
	COST_PCT("Cost,Percent"),
	COST_REDUCTION_PCT("CostReduction,Percent"),
	CAST_TIME("CastTime"),
	DURATION_PCT("Duration,Percent"),
	DURATION("Duration"),
	COOLDOWN_PCT("Cooldown,Percent"),
	COOLDOWN("Cooldown"),
	THREAT_PCT("Threat,Percent"),
	PUSHBACK_PCT("Pushback,Percent"),
	RANGE_PCT("Range,Percent"),
	MAX_HEALTH("MaxHealth"),
	MAX_HEALTH_PCT("MaxHealth,Percent"),
	MAX_MANA("MaxMana"),
	MAX_MANA_PCT("MaxMana,Percent"),
	MANA_TRANSFERRED_TO_PET_PCT("ManaTransferredToPet,Percent"),
	PET_STAMINA("Pet,Stamina"),
	PET_STAMINA_PCT("Pet,Stamina,Percent"),
	PET_INTELLECT("Pet,Intellect"),
	PET_INTELLECT_PCT("Pet,Intellect,Percent"),
	PET_CRIT_PCT("Pet,Crit,Percent"),
	PET_DAMAGE_PCT("Pet,Damage,Percent"),
	PET_DAMAGE_MELEE_PCT("Pet,Damage,Melee,Percent"),
	PET_MAX_MANA_PCT("Pet,MaxMana,Percent");

	private final String key;
	private final PrimitiveAttributeType type;
	private final PowerType powerType;
	private final DamageType damageType;
	private final boolean petAttribute;
	private final ValueType valueType;

	PrimitiveAttributeId(String key) {
		Parser parser = new Parser(key);

		this.key = key;
		this.type = parser.type;
		this.powerType = parser.powerType;
		this.damageType = parser.damageType;
		this.petAttribute = parser.petAttribute;
		this.valueType = parser.valueType;
	}

	public static PrimitiveAttributeId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static PrimitiveAttributeId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}

	private static class Parser {
		private final String line;

		private PrimitiveAttributeType type;
		private PowerType powerType;
		private ValueType valueType;
		private DamageType damageType;
		private boolean petAttribute;

		public Parser(String line) {
			this.line = line;
			parse();
		}

		private void parse() {
			for (String part : line.split(",")) {
				parsePart(part);
			}
			ensureCorrectDefaults();
		}

		private void parsePart(String part) {
			if (type == null) {
				var parsedType = PrimitiveAttributeType.tryParse(part);
				if (parsedType != null) {
					this.type = parsedType;
					return;
				}
			}

			if (valueType == null) {
				var parsedValueType = ValueType.tryParse(part);
				if (parsedValueType != null) {
					this.valueType = parsedValueType;
					return;
				}
			}

			if (powerType == null) {
				var parsedPowerType = PowerType.tryParse(part);
				if (parsedPowerType != null) {
					this.powerType = parsedPowerType;
					return;
				}
			}

			if (damageType == null) {
				var parsedDamageType = DamageType.tryParse(part);
				if (parsedDamageType != null) {
					this.damageType = parsedDamageType;
					return;
				}
			}

			if (part.equalsIgnoreCase("Pet")) {
				this.petAttribute = true;
				return;
			}

			error("Can't parse: " + part);
		}

		private void ensureCorrectDefaults() {
			if (type == null) {
				error("No type");
			}

			if (valueType == null) {
				this.valueType = getDefaultValueType();
			}

			if (!type.getAcceptedValueTypes().contains(valueType)) {
				error("Incorrect value type");
			}

			if (powerType == null) {
				powerType = PowerType.ANY;
			}

			if (!type.getAcceptedPowerTypes().contains(powerType)) {
				error("Not allowed power type");
			}

			if (damageType == null) {
				damageType = DamageType.ANY;
			}

			if (!type.getAcceptedDamageTypes().contains(damageType)) {
				error("Not allowed damage type");
			}

			if (petAttribute && !type.isPetsAllowed()) {
				error("Not allowed for pets");
			}
		}

		private ValueType getDefaultValueType() {
			var acceptedValueTypes = type.getAcceptedValueTypes();

			if (acceptedValueTypes.contains(ValueType.POINT) && !acceptedValueTypes.contains(ValueType.DURATION)) {
				return ValueType.POINT;
			} else if (acceptedValueTypes.contains(ValueType.DURATION) && !acceptedValueTypes.contains(ValueType.POINT)) {
				return ValueType.DURATION;
			}

			return error("Can't determine default value type");
		}

		private <T> T error(String msg) {
			throw new IllegalArgumentException(msg + ", line: " + line);
		}
	}
}
