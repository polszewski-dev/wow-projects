package wow.commons.model.profession;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2020-07-14
 */
@AllArgsConstructor
public enum ProfessionId {
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
	FIRST_AID("First Aid");

	private final String name;

	public static ProfessionId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static ProfessionId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
