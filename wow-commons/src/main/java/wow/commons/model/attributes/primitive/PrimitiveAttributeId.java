package wow.commons.model.attributes.primitive;

import wow.commons.model.attributes.AttributeId;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
public enum PrimitiveAttributeId implements AttributeId {
	STRENGTH("Strength", "str"),
	AGILITY("Agility", "agi"),
	STAMINA("Stamina", "sta"),
	INTELLECT("Intellect", "int"),
	SPIRIT("Spirit", "spi"),
	BASE_STATS_INCREASE("BaseStatsIncrease", "base stats"),

	ATTACK_POWER("AttackPower"),
	RANGED_ATTACK_POWER("RangedAttackPower"),
	CRIT_RATING("CritRating"),
	HIT_RATING("HitRating"),
	HASTE_RATING("HasteRating"),
	EXPERTISE_RATING("ExpertiseRating"),
	ARMOR_PENETRATION("ArmorPenetration"),

	SPELL_POWER("SpellPower", "sp"),
	SPELL_DAMAGE("SpellDamage", "sd"),
	SPELL_CRIT_RATING("SpellCritRating", "crit"),
	SPELL_HIT_RATING("SpellHitRating", "hit"),
	SPELL_HASTE_RATING("SpellHasteRating", "haste"),
	SPELL_PENETRATION("SpellPenetration", "penetration"),

	ADDITIONAL_DAMAGE("AdditionalDamage", "additional damage"),

	HEALING_POWER("HealingPower", "healing"),

	MP5("Mp5", "mp5"),
	HP5("Hp5", "hp5"),

	ARMOR("Armor", "armor"),
	DODGE("Dodge"),
	DODGE_RATING("DodgeRating"),
	DEFENSE("Defense"),
	DEFENSE_RATING("DefenseRating"),
	BLOCK("Block"),

	BLOCK_RATING("BlockRating"),
	SHIELD_BLOCK("ShieldBlock"),
	SHIELD_BLOCK_RATING("ShieldBlockRating"),
	PARRY("Parry"),
	PARRY_RATING("ParryRating"),
	RESILIENCE_RATING("ResilienceRating", "resi"),

	RESISTANCE("Resistance", "resist"),
	RESIST_ALL("ResistAll", "resist all"),

	EXTRA_CRIT_COEFF("ExtraCritCoeff", "gimmick to handle ISB"),

	// percent attributes

	BASE_STATS_INCREASE_PCT("BaseStatsIncreasePct", "base stats"),
	CRIT_PCT("CritPct"),
	HIT_PCT("HitPct"),
	ADDITIONAL_SPELL_DAMAGE_TAKEN_PCT("AdditionalSpellDamageTakenPct", "additinal spell dmg taken"),
	SPELL_CRIT_PCT("SpellCritPct", "crit"),
	SPELL_HIT_PCT("SpellHitPct", "hit"),
	SPELL_HASTE_PCT("SpellHastePct", "haste"),
	INCREASED_CRITICAL_DAMAGE_PCT("IncreasedCriticalDamagePct", "increased crit dmg"),
	DAMAGE_TAKEN_PCT("DamageTakenPct", "increased spell dmg"),
	BLOCK_PCT("BlockPct"),
	SPEED_INCREASE_PCT("SpeedIncreasePct", "movement speed"),
	COST_REDUCTION_PCT("CostReductionPct", "reduced cost"),
	THREAT_REDUCTION_PCT("ThreatReductionPct", "reduced threat"),
	PUSHBACK_REDUCTION_PCT("PushbackReductionPct", "reduced pushback"),
	RANGE_INCREASE_PCT("RangeIncreasePct", "increased range"),
	DURATION_INCREASE_PCT("DurationIncreasePct", "increased duration"),
	SPELL_COEFF_BONUS_PCT("SpellCoeffBonusPct", "increased spell coefficient"),
	EFFECT_INCREASE_PCT("EffectIncreasePct", "increased effect"),
	DIRECT_DAMAGE_INCREASE_PCT("DirectDamageIncreasePct", "increased direct damage"),
	DOT_DAMAGE_INCREASE_PCT("DotDamageIncreasePct", "increased dot damage"),
	CRIT_DAMAGE_INCREASE_PCT("CritDamageIncreasePct", "increased crit damage"),

	STA_INCREASE_PCT("StaIncreasePct", "increased sta"),
	INT_INCREASE_PCT("IntIncreasePct", "increased int"),
	SPI_INCREASE_PCT("SpiIncreasePct", "increased spi"),
	MAX_HEALTH_INCREASE_PCT("MaxHealthIncreasePct", "increased max health"),
	MAX_MANA_INCREASE_PCT("MaxManaIncreasePct", "increased max mana"),
	MELEE_CRIT_INCREASE_PCT("MeleeCritIncreasePct"),
	DAMAGE_TAKEN_INCREASE_PCT("DamageTakenIncreasePct"),

	PET_STA_INCREASE_PCT("PetStaIncreasePct", "increased pet sta"),
	PET_INT_INCREASE_PCT("PetIntIncreasePct", "increased pet int"),
	PET_SPELL_CRIT_INCREASE_PCT("PetSpellCritIncreasePct"),
	PET_MELEE_CRIT_INCREASE_PCT("PetMeleeCritIncreasePct"),
	PET_MELEE_DAMAGE_INCREASE_PCT("PetMeleeDamageIncreasePct"),

	MANA_TRANSFERRED_TO_PET_PCT("ManaTransferredToPetPct"),

	// duration attributes

	CAST_TIME_REDUCTION("CastTimeReduction", "reduced cast time"),
	COOLDOWN_REDUCTION("CooldownReduction"),

	// boolean attributes

	NUM_NEXT_SPELLS_CAST_INSTANTLY("#NextSpellsCastInstantly");

	private final String key;
	private final String shortName;

	PrimitiveAttributeId(String key, String shortName) {
		this.key = key;
		this.shortName = shortName;
	}

	PrimitiveAttributeId(String key) {
		this(key, null);
	}

	public static PrimitiveAttributeId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static PrimitiveAttributeId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public boolean isPrimitiveAttribute() {
		return true;
	}

	@Override
	public int getSortOrder() {
		return ordinal();
	}

	@Override
	public String toString() {
		return shortName != null ? shortName : key;
	}
}
