package wow.commons.model.profession;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public enum ProfessionSpecializationId {
	ELIXIR_MASTER,
	POTION_MASTER,
	TRANSMUTATION_MASTER,
	SPELLFIRE_TAILORING,
	SHADOWEAVE_TAILORING,
	MOONCLOTH_TAILORING,
	DRAGONSCALE_LEATHERWORKING,
	ELEMENTAL_LEATHERWORKING,
	TRIBAL_LEATHERWORKING,
	ARMORSMITH,
	WEAPONSMITH,
	MASTER_SWORDSMITH,
	MASTER_AXESMITH,
	MASTER_HAMMERSMITH,
	GOBLIN_ENGINEER,
	GNOMISH_ENGINEER;

	public static ProfessionSpecializationId parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public static ProfessionSpecializationId tryParse(String value) {
		return EnumUtil.tryParse(value, values());
	}
}
