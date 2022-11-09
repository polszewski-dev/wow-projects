package wow.commons.model.attributes.primitive;

import wow.commons.model.attributes.AttributeId;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
public enum DoubleAttributeId implements AttributeId {
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

	EXTRA_CRIT_COEFF("ExtraCritCoeff", "gimmick to handle ISB"),;

	private final String key;
	private final String shortName;

	DoubleAttributeId(String key, String shortName) {
		this.key = key;
		this.shortName = shortName;
	}

	DoubleAttributeId(String key) {
		this(key, null);
	}

	public static DoubleAttributeId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static DoubleAttributeId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public boolean isDoubleAttribute() {
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
