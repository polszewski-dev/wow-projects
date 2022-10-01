package wow.commons.model.categorization;

/**
 * User: POlszewski
 * Date: 2021-03-03
 */
public enum ItemRarity {
	Common("cffffffff"),
	Uncommon("cff1eff00"),
	Rare("cff0070dd"),
	Epic("cffa335ee"),
	Legendary("cffff8000"),
	;

	private final String color;

	ItemRarity(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public static ItemRarity parseFromItemLink(String link) {
		for (ItemRarity itemRarity : values()) {
			if (link.contains("|" + itemRarity.color + "|")) {
				return itemRarity;
			}
		}
		throw new IllegalArgumentException("Can't parse rarity from: " + link);
	}

	public boolean isAtLeastAsGoodAs(ItemRarity itemRarity) {
		return this.ordinal() >= itemRarity.ordinal();
	}
}
