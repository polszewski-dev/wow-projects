package wow.commons.model.attributes.primitive;

import lombok.AllArgsConstructor;
import wow.commons.model.attributes.AttributeId;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
@AllArgsConstructor
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

	DURATION_INCREASE("DurationIncrease", "increased duration", DisplayHint.DURATION),

	NUM_NEXT_SPELLS_CAST_INSTANTLY("#NextSpellsCastInstantly"),

	// percent attributes

	BASE_STATS_INCREASE_PCT("BaseStatsIncreasePct", "base stats", DisplayHint.PERCENT),
	CRIT_PCT("CritPct", DisplayHint.PERCENT),
	HIT_PCT("HitPct", DisplayHint.PERCENT),
	ADDITIONAL_SPELL_DAMAGE_TAKEN_PCT("AdditionalSpellDamageTakenPct", "additinal spell dmg taken", DisplayHint.PERCENT),
	SPELL_CRIT_PCT("SpellCritPct", "crit", DisplayHint.PERCENT),
	SPELL_HIT_PCT("SpellHitPct", "hit", DisplayHint.PERCENT),
	SPELL_HASTE_PCT("SpellHastePct", "haste", DisplayHint.PERCENT),
	INCREASED_CRITICAL_DAMAGE_PCT("IncreasedCriticalDamagePct", "increased crit dmg", DisplayHint.PERCENT),
	DAMAGE_TAKEN_PCT("DamageTakenPct", "increased spell dmg", DisplayHint.PERCENT),
	BLOCK_PCT("BlockPct", DisplayHint.PERCENT),
	SPEED_INCREASE_PCT("SpeedIncreasePct", "movement speed", DisplayHint.PERCENT),
	COST_REDUCTION_PCT("CostReductionPct", "reduced cost", DisplayHint.PERCENT),
	THREAT_REDUCTION_PCT("ThreatReductionPct", "reduced threat", DisplayHint.PERCENT),
	PUSHBACK_REDUCTION_PCT("PushbackReductionPct", "reduced pushback", DisplayHint.PERCENT),
	RANGE_INCREASE_PCT("RangeIncreasePct", "increased range", DisplayHint.PERCENT),
	DURATION_INCREASE_PCT("DurationIncreasePct", "increased duration", DisplayHint.PERCENT),
	SPELL_COEFF_BONUS_PCT("SpellCoeffBonusPct", "increased spell coefficient", DisplayHint.PERCENT),
	EFFECT_INCREASE_PCT("EffectIncreasePct", "increased effect", DisplayHint.PERCENT),
	DIRECT_DAMAGE_INCREASE_PCT("DirectDamageIncreasePct", "increased direct damage", DisplayHint.PERCENT),
	DOT_DAMAGE_INCREASE_PCT("DotDamageIncreasePct", "increased dot damage", DisplayHint.PERCENT),
	CRIT_DAMAGE_INCREASE_PCT("CritDamageIncreasePct", "increased crit damage", DisplayHint.PERCENT),

	STA_INCREASE_PCT("StaIncreasePct", "increased sta", DisplayHint.PERCENT),
	INT_INCREASE_PCT("IntIncreasePct", "increased int", DisplayHint.PERCENT),
	SPI_INCREASE_PCT("SpiIncreasePct", "increased spi", DisplayHint.PERCENT),
	MAX_HEALTH_INCREASE_PCT("MaxHealthIncreasePct", "increased max health", DisplayHint.PERCENT),
	MAX_MANA_INCREASE_PCT("MaxManaIncreasePct", "increased max mana", DisplayHint.PERCENT),
	MELEE_CRIT_INCREASE_PCT("MeleeCritIncreasePct", DisplayHint.PERCENT),
	DAMAGE_TAKEN_INCREASE_PCT("DamageTakenIncreasePct", DisplayHint.PERCENT),

	PET_STA_INCREASE_PCT("PetStaIncreasePct", "increased pet sta", DisplayHint.PERCENT),
	PET_INT_INCREASE_PCT("PetIntIncreasePct", "increased pet int", DisplayHint.PERCENT),
	PET_SPELL_CRIT_INCREASE_PCT("PetSpellCritIncreasePct", DisplayHint.PERCENT),
	PET_MELEE_CRIT_INCREASE_PCT("PetMeleeCritIncreasePct", DisplayHint.PERCENT),
	PET_MELEE_DAMAGE_INCREASE_PCT("PetMeleeDamageIncreasePct", DisplayHint.PERCENT),

	MANA_TRANSFERRED_TO_PET_PCT("ManaTransferredToPetPct", DisplayHint.PERCENT),

	// duration attributes

	CAST_TIME_REDUCTION("CastTimeReduction", "reduced cast time", DisplayHint.DURATION),
	COOLDOWN_REDUCTION("CooldownReduction", DisplayHint.DURATION),
	COOLDOWN_REDUCTION_PCT("CooldownReductionPct", DisplayHint.PERCENT);

	enum DisplayHint {
		PERCENT,
		DURATION
	}

	private final String key;
	private final String shortName;
	private final DisplayHint displayHint;

	PrimitiveAttributeId(String key) {
		this(key, null, null);
	}

	PrimitiveAttributeId(String key, DisplayHint displayHint) {
		this(key, null, displayHint);
	}

	PrimitiveAttributeId(String key, String shortName) {
		this(key, shortName, null);
	}

	public static PrimitiveAttributeId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static PrimitiveAttributeId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	public String getShortName() {
		return shortName != null ? shortName : key;
	}

	public DisplayHint getDisplayHint() {
		return displayHint;
	}

	@Override
	public String toString() {
		return key;
	}
}
