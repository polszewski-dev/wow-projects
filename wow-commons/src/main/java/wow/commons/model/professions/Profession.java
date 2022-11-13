package wow.commons.model.professions;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2020-07-14
 */
@AllArgsConstructor
public enum Profession {
	ENCHANTING("Enchanting"),
	JEWELCRAFTING("Jewelcrafting"),
	INSCRIPTION("Inscription"),
	ALCHEMY("Alchemy"),
	TAILORING("Tailoring"),
	LEATHERWORKING("Leatherworking"),
	BLACKSMITHING("Blacksmithing"),
	ENGINEERING("Engineering"),
	HERBALISM("Herbalism"),
	MINING("Mining"),
	SKINNING("Skinning"),
	COOKING("Cooking"),
	FISHING("Fishing"),
	FIRST_AID("FirstAid");

	private final String key;

	public static Profession parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}
