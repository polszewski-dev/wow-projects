package wow.commons.model.attributes;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.util.EnumUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-04
 */
public enum AttributeId {
	STRENGTH("Strength", "str"),
	AGILITY("Agility", "agi"),
	STAMINA("Stamina", "sta"),
	INTELLECT("Intellect", "int"),
	SPIRIT("Spirit", "spi"),

	BASE_STATS_INCREASE("BaseStatsIncrease", "base stats"),
	BASE_STATS_INCREASE_PCT("BaseStatsIncreasePct", Percent.class, "base stats"),

	ATTACK_POWER("AttackPower"),
	RANGED_ATTACK_POWER("RangedAttackPower"),
	CRIT_PCT("CritPct", Percent.class),
	CRIT_RATING("CritRating"),
	HIT_PCT("HitPct", Percent.class),
	HIT_RATING("HitRating"),
	HASTE_RATING("HasteRating"),
	EXPERTISE_RATING("ExpertiseRating"),
	ARMOR_PENETRATION("ArmorPenetration"),

	SPELL_POWER("SpellPower", "sp"),
	SPELL_DAMAGE("SpellDamage", "sd"),

	ADDITIONAL_SPELL_DAMAGE_TAKEN_PCT("AdditionalSpellDamageTakenPct", Percent.class, "additinal spell dmg taken"),

	SPELL_CRIT_RATING("SpellCritRating", "crit"),
	SPELL_CRIT_PCT("SpellCritPct", Percent.class, "crit"),
	SPELL_HIT_RATING("SpellHitRating", "hit"),
	SPELL_HIT_PCT("SpellHitPct", Percent.class, "hit"),
	SPELL_HASTE_RATING("SpellHasteRating", "haste"),
	SPELL_HASTE_PCT("SpellHastePct", Percent.class, "haste"),
	SPELL_PENETRATION("SpellPenetration", "penetration"),

	INCREASED_CRITICAL_DAMAGE_PCT("IncreasedCriticalDamagePct", Percent.class, "increased crit dmg"),
	DAMAGE_TAKEN_PCT("DamageTakenPct", Percent.class, "increased spell dmg"),

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
	BLOCK_PCT("BlockPct", Percent.class),
	BLOCK_RATING("BlockRating"),
	SHIELD_BLOCK("ShieldBlock"),
	SHIELD_BLOCK_RATING("ShieldBlockRating"),
	PARRY("Parry"),
	PARRY_RATING("ParryRating"),
	RESILIENCE_RATING("ResilienceRating", "resi"),

	RESISTANCE("Resistance", "resist"),
	RESIST_ALL("ResistAll", "resist all"),

	SPEED_INCREASE_PCT("SpeedIncreasePct", Percent.class, "movement speed"),

	//

	SPECIAL_ABILITIES("SpecialAbilities", ComplexAttribute.class),

	SOCKETS("Sockets", ComplexAttribute.class),
	SET_PIECES("SetPieces", ComplexAttribute.class),

	// talents

	CAST_TIME_REDUCTION("CastTimeReduction", Duration.class, "reduced cast time"),//
	COOLDOWN_REDUCTION("CooldownReduction", Duration.class),
	COST_REDUCTION_PCT("CostReductionPct", Percent.class, "reduced cost"),//
	THREAT_REDUCTION_PCT("ThreatReductionPct", Percent.class, "reduced threat"),
	PUSHBACK_REDUCTION_PCT("PushbackReductionPct", Percent.class, "reduced pushback"),
	RANGE_INCREASE_PCT("RangeIncreasePct", Percent.class, "increased range"),
	DURATION_INCREASE_PCT("DurationIncreasePct", Percent.class, "increased duration"),//
	CAST_INSTANTLY("CastInstantly", Boolean.class),

	SPELL_COEFF_BONUS_PCT("SpellCoeffBonusPct", Percent.class, "increased spell coefficient"),//
	EFFECT_INCREASE_PCT("EffectIncreasePct", Percent.class, "increased effect"),//
	DIRECT_DAMAGE_INCREASE_PCT("DirectDamageIncreasePct", Percent.class, "increased direct damage"),//
	DOT_DAMAGE_INCREASE_PCT("DotDamageIncreasePct", Percent.class, "increased dot damage"),//
	CRIT_DAMAGE_INCREASE_PCT("CritDamageIncreasePct", Percent.class, "increased crit damage"),//Ruin!!
	EXTRA_CRIT_COEFF("ExtraCritCoeff", "gimmick to handle ISB"),

	STA_INCREASE_PCT("StaIncreasePct", Percent.class, "increased sta"),
	INT_INCREASE_PCT("IntIncreasePct", Percent.class, "increased int"),
	SPI_INCREASE_PCT("SpiIncreasePct", Percent.class, "increased spi"),
	MAX_HEALTH_INCREASE_PCT("MaxHealthIncreasePct", Percent.class, "increased max health"),
	MAX_MANA_INCREASE_PCT("MaxManaIncreasePct", Percent.class, "increased max mana"),
	MELEE_CRIT_INCREASE_PCT("MeleeCritIncreasePct", Percent.class),
	DAMAGE_TAKEN_INCREASE_PCT("DamageTakenIncreasePct", Percent.class),//

	PET_STA_INCREASE_PCT("PetStaIncreasePct", Percent.class, "increased pet sta"),
	PET_INT_INCREASE_PCT("PetIntIncreasePct", Percent.class, "increased pet int"),
	PET_SPELL_CRIT_INCREASE_PCT("PetSpellCritIncreasePct", Percent.class),
	PET_MELEE_CRIT_INCREASE_PCT("PetMeleeCritIncreasePct", Percent.class),
	PET_MELEE_DAMAGE_INCREASE_PCT("PetMeleeDamageIncreasePct", Percent.class),

	STAT_CONVERSION("StatConversion", ComplexAttribute.class),//
	EFFECT_INCREASE_PER_EFFECT_ON_TARGET("EffectIncreasePerEffectOnTarget", ComplexAttribute.class),

	MANA_TRANSFERRED_TO_PET_PCT("ManaTransferredToPetPct", Percent.class);

	private final String key;
	private final Class type;
	private final String shortName;

	private static final List<AttributeId> complexAttributeIds = Stream.of(values())
			.filter(AttributeId::isComplexAttribute)
			.collect(Collectors.toUnmodifiableList());

	AttributeId(String key, Class type, String shortName) {
		List<Class> allowedTypes = List.of(Double.class, Percent.class, Boolean.class, Duration.class, ComplexAttribute.class);
		if (!allowedTypes.contains(type)) {
			throw new IllegalArgumentException();
		}
		this.key = key;
		this.type = type;
		this.shortName = shortName;
	}

	AttributeId(String key, Class type) {
		this(key, type, null);
	}

	AttributeId(String key, String shortName) {
		this(key, Double.class, shortName);
	}

	AttributeId(String key) {
		this(key, Double.class, null);
	}

	public static AttributeId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public Class getType() {
		return type;
	}

	public boolean isDoubleAttribute() {
		return type == Double.class;
	}

	public boolean isPercentAttribute() {
		return type == Percent.class;
	}

	public boolean isBooleanAttribute() {
		return type == Boolean.class;
	}

	public boolean isDurationAttribute() {
		return type == Duration.class;
	}

	public boolean isScalarAttribute() {
		return isDoubleAttribute() || isPercentAttribute() || isDurationAttribute();
	}

	public boolean isPrimitiveAttribute() {
		return !isComplexAttribute();
	}

	public boolean isComplexAttribute() {
		return type == ComplexAttribute.class;
	}
	
	public static List<AttributeId> getComplexAttributeIds() {
		return complexAttributeIds;
	}

	@Override
	public String toString() {
		return shortName != null ? shortName : name();
	}
}
