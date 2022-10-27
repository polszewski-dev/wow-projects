package wow.scraper.model;

import wow.commons.model.categorization.ItemRarity;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
public enum WowheadItemQuality {
	Common(1, ItemRarity.Common),
	Uncommon(2, ItemRarity.Uncommon),
	Rare(3, ItemRarity.Rare),
	Epic(4, ItemRarity.Epic),
	Legendary(5, ItemRarity.Legendary);

	private final int code;
	private final ItemRarity itemRarity;

	WowheadItemQuality(int code, ItemRarity itemRarity) {
		this.code = code;
		this.itemRarity = itemRarity;
	}

	public static WowheadItemQuality fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}

	public int getCode() {
		return code;
	}

	public ItemRarity getItemRarity() {
		return itemRarity;
	}
}
