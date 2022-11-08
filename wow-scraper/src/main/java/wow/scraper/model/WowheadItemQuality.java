package wow.scraper.model;

import wow.commons.model.categorization.ItemRarity;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
public enum WowheadItemQuality {
	COMMON(1, ItemRarity.COMMON),
	UNCOMMON(2, ItemRarity.UNCOMMON),
	RARE(3, ItemRarity.RARE),
	EPIC(4, ItemRarity.EPIC),
	LEGENDARY(5, ItemRarity.LEGENDARY);

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
