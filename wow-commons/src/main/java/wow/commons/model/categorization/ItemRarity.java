package wow.commons.model.categorization;

/**
 * User: POlszewski
 * Date: 2021-03-03
 */
public enum ItemRarity {
	Common("ffffffff"),
	Uncommon("ff1eff00"),
	Rare("ff0070dd"),
	Epic("ffa335ee"),
	Legendary("ffff8000"),
	;

	private final String color;

	ItemRarity(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
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
