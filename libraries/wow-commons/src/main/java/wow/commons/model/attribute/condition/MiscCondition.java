package wow.commons.model.attribute.condition;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-09-23
 */
@AllArgsConstructor
public enum MiscCondition implements AttributeCondition {
	SPELL("Spell"),
	PHYSICAL("Physical"),

	SPELL_DAMAGE("SpellDamage"),
	HEALING("Healing"),
	MELEE("Melee"),
	RANGED("Ranged"),
	WEAPON("Weapon"),

	DIRECT("Direct"),
	PERIODIC("Periodic"),

	HAS_DAMAGING_COMPONENT("HasDamagingComponent"),
	HAS_HEALING_COMPONENT("HasHealingComponent"),

	HOSTILE_SPELL("HostileSpell"),
	NORMAL_MELEE_ATTACK("NormalMeleeAttack"),
	SPECIAL_ATTACK("SpecialAttack"),

	HAS_MANA_COST("HasManaCost"),
	HAS_CAST_TIME("HasCastTime"),
	IS_INSTANT_CAST("IsInstantCast"),
	HAS_CAST_TIME_UNDER_10_SEC("HasCastTimeUnder10Sec"),

	CAN_CRIT("CanCrit"),
	HAD_CRITICAL("HadCrit"),
	HAD_NO_CRITICAL("HadNoCrit"),

	HAS_PET("HasPet"),

	TARGETING_OTHERS("TargetingOthers"),

	OWNER_HEALTH_BELOW_20_PCT("OwnerHealthBelow20%"),
	OWNER_HEALTH_BELOW_35_PCT("OwnerHealthBelow35%"),
	OWNER_HEALTH_BELOW_40_PCT("OwnerHealthBelow40%"),
	OWNER_HEALTH_BELOW_70_PCT("OwnerHealthBelow70%"),
	TARGET_HEALTH_BELOW_50_PCT("TargetHealthBelow50%"),

	;

	private final String key;

	public static MiscCondition parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static MiscCondition tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}
