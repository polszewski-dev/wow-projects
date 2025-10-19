package wow.commons.model.attribute;

import lombok.Getter;
import wow.commons.util.EnumUtil;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-04
 */
@Getter
public enum AttributeId {
	STRENGTH("Strength"),
	STRENGTH_PCT("Strength%"),
	AGILITY("Agility"),
	AGILITY_PCT("Agility%"),
	STAMINA("Stamina"),
	STAMINA_PCT("Stamina%"),
	INTELLECT("Intellect"),
	INTELLECT_PCT("Intellect%"),
	SPIRIT("Spirit"),
	SPIRIT_PCT("Spirit%"),

	BASE_STATS("BaseStats"),
	BASE_STATS_PCT("BaseStats%"),

	MAX_HEALTH("MaxHealth"),
	MAX_HEALTH_PCT("MaxHealth%"),
	MAX_MANA("MaxMana"),
	MAX_MANA_PCT("MaxMana%"),

	DAMAGE("Damage"),
	DAMAGE_PCT("Damage%"),
	HEALING("Healing"),
	HEALING_PCT("Healing%"),
	ABSORPTION("Absorption"),
	ABSORPTION_PCT("Absorption%"),
	EFFECT_PCT("Effect%"),

	POWER("Power"),
	POWER_PCT("Power%"),
	POWER_COEFFICIENT_PCT("PowerCoeff%"),

	HIT_RATING("HitRating"),
	HIT_PCT("Hit%"),
	CRIT_RATING("CritRating"),
	CRIT_PCT("Crit%"),
	HASTE_RATING("HasteRating"),
	HASTE_PCT("Haste%"),

	CRIT_DAMAGE_PCT("CritDamage%"),
	CRIT_DAMAGE_MULTIPLIER_PCT("CritDamageMultiplier%"),
	CRIT_COEFF_PCT("CritCoeff%"),

	DAMAGE_TAKEN("DamageTaken"),
	DAMAGE_TAKEN_PCT("DamageTaken%"),
	HEALING_TAKEN("HealingTaken"),
	HEALING_TAKEN_PCT("HealingTaken%"),
	POWER_TAKEN("PowerTaken"),
	CRIT_TAKEN_PCT("CritTaken%"),

	PENETRATION("Penetration"),
	EXPERTISE_RATING("ExpertiseRating"),

	MP5("Mp5"),
	HP5("Hp5"),

	ARMOR("Armor"),
	ARMOR_PCT("Armor%"),
	DODGE_RATING("DodgeRating"),
	DODGE_PCT("Dodge%"),
	DEFENSE("Defense"),
	DEFENSE_RATING("DefenseRating"),
	BLOCK("Block"),
	BLOCK_RATING("BlockRating"),
	BLOCK_PCT("Block%"),
	SHIELD_BLOCK("ShieldBlock"),
	SHIELD_BLOCK_RATING("ShieldBlockRating"),
	SHIELD_BLOCK_PCT("ShieldBlock%"),
	PARRY_PCT("Parry%"),
	PARRY_RATING("ParryRating"),

	RESILIENCE_RATING("ResilienceRating"),
	RESISTANCE("Resistance"),
	RESISTANCE_PCT("Resistance%"),
	EFFECT_RESIST("EffectResist%"),

	SPEEED_PCT("Speed%"),

	MANA_COST("ManaCost"),
	MANA_COST_PCT("ManaCost%"),
	ENERGY_COST("EnergyCost"),
	ENERGY_COST_PCT("EnergyCost%"),
	RAGE_COST("RageCost"),
	RAGE_COST_PCT("RageCost%"),
	HEALTH_COST("HealthCost"),
	HEALTH_COST_PCT("HealthCost%"),
	COST_REDUCTION_CT("CostReduction%"),

	CAST_TIME("CastTime"),
	CAST_TIME_PCT("CastTime%"),
	DURATION_PCT("Duration%"),
	DURATION("Duration"),
	COOLDOWN_PCT("Cooldown%"),
	COOLDOWN("Cooldown"),

	RANGE("Range"),
	RANGE_PCT("Range%"),
	THREAT("Threat"),
	THREAT_PCT("Threat%"),
	PUSHBACK_PCT("Pushback%"),

	RECEIVED_EFFECT_DURATION("ReceivedEffectDuration"),
	RECEIVED_EFFECT_DURATION_PCT("ReceivedEffectDuration%"),

	HEALTH_REGEN_PCT("HealthRegen%"),
	MANA_REGEN_PCT("ManaRegen%"),
	IN_COMBAT_HEALTH_REGEN_PCT("InCombatHealthRegen%"),
	IN_COMBAT_MANA_REGEN_PCT("InCombatManaRegen%"),

	WEAPON_SKILL("WeaponSkill"),
	SKILL("Skill"),

	DAMAGE_TAKEN_TRANSFERRED_TO_PET_PCT("DamageTakenTransferredToPet%"),
	SPELL_REFLECT_PCT("SpellReflect%"),
	CHAIN_MULTIPLIER("ChainMultiplier%"),


	PET_STAMINA("Pet.Stamina"),
	PET_STAMINA_PCT("Pet.Stamina%"),
	PET_INTELLECT("Pet.Intellect"),
	PET_INTELLECT_PCT("Pet.Intellect%"),
	PET_MAX_MANA_PCT("Pet.MaxMana%"),

	PET_DAMAGE_PCT("Pet.Damage%"),
	PET_POWER("Pet.Power"),
	PET_CRIT_PCT("Pet.Crit%"),
	PET_MP5("Pet.Mp5"),
	PET_RESISTANCE("Pet.Resistance"),
	PET_DAMAGETAKEN_PCT("Pet.DamageTaken%"),

	PARTY_POWER("Party.Power"),
	PARTY_HIT_PCT("Party.Hit%"),
	PARTY_CRIT_RATING("Party.CritRating"),
	PARTY_CRIT_PCT("Party.Crit%"),
	PARTY_MP5("Party.Mp5"),

	COPY_PCT("Copy%")
	;

	private final String key;
	private final AttributeType type;
	private final ValueType valueType;
	private final AttributeTarget target;

	AttributeId(String key) {
		Parser parser = new Parser(key);

		this.key = key;
		this.type = parser.type;
		this.valueType = parser.valueType;
		this.target = parser.target;

		Objects.requireNonNull(this.key);
		Objects.requireNonNull(this.type);
		Objects.requireNonNull(this.valueType);
		Objects.requireNonNull(this.target);
	}

	public static AttributeId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static AttributeId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}

	private static class Parser {
		private String line;

		private AttributeType type;
		private ValueType valueType;
		private AttributeTarget target;

		public Parser(String line) {
			this.line = line;
			parse();
		}

		private void parse() {
			this.valueType = parseValueType();
			this.target = parseTarget();
			this.type = AttributeType.parse(line);

			ensureCorrectDefaults();
		}

		private ValueType parseValueType() {
			final var percent = "%";
			final var rating = "Rating";

			if (line.endsWith(percent)) {
				replace(percent);
				return ValueType.PERCENT;
			}
			if (line.endsWith(rating)) {
				replace(rating);
				return ValueType.RATING;
			} else {
				return null;
			}
		}

		private AttributeTarget parseTarget() {
			final var pet = "Pet.";
			final var party = "Party.";

			if (line.startsWith(pet)) {
				replace(pet);
				return AttributeTarget.PET;
			}
			if (line.startsWith(party)) {
				replace(party);
				return AttributeTarget.PARTY;
			}
			return AttributeTarget.OWNER;
		}

		private void replace(String textToReplace) {
			this.line = line.replace(textToReplace, "");
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
		}

		private ValueType getDefaultValueType() {
			var acceptedValueTypes = type.getAcceptedValueTypes();

			if (acceptedValueTypes.contains(ValueType.POINT) && !acceptedValueTypes.contains(ValueType.DURATION)) {
				return ValueType.POINT;
			} else if (acceptedValueTypes.contains(ValueType.DURATION) && !acceptedValueTypes.contains(ValueType.POINT)) {
				return ValueType.DURATION;
			}

			error("Can't determine default value type");
			return null;
		}

		private void error(String msg) {
			throw new IllegalArgumentException(msg + ", line: " + line);
		}
	}
}
