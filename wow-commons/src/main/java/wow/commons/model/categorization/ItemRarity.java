package wow.commons.model.categorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-03
 */
@AllArgsConstructor
@Getter
public enum ItemRarity {
	COMMON("ffffffff"),
	UNCOMMON("ff1eff00"),
	RARE("ff0070dd"),
	EPIC("ffa335ee"),
	LEGENDARY("ffff8000");

	private final String color;

	public static ItemRarity parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public static ItemRarity parseFromColor(String color) {
		for (ItemRarity itemRarity : values()) {
			if (itemRarity.color.equalsIgnoreCase(color)) {
				return itemRarity;
			}
		}
		throw new IllegalArgumentException("Can't parse rarity from: " + color);
	}

	public boolean isAtLeastAsGoodAs(ItemRarity itemRarity) {
		return this.ordinal() >= itemRarity.ordinal();
	}
}
