package wow.commons.model.professions;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2020-07-14
 */
public enum ProfessionId {
	ENCHANTING,
	JEWELCRAFTING,
	INSCRIPTION,
	ALCHEMY,
	TAILORING,
	LEATHERWORKING,
	BLACKSMITHING,
	ENGINEERING,
	HERBALISM,
	MINING,
	SKINNING,
	COOKING,
	FISHING,
	FIRST_AID;

	public static ProfessionId parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
