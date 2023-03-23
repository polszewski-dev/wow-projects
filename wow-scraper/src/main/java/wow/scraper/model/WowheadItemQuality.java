package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.categorization.ItemRarity;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@AllArgsConstructor
@Getter
public enum WowheadItemQuality {
	COMMON(1, ItemRarity.COMMON),
	UNCOMMON(2, ItemRarity.UNCOMMON),
	RARE(3, ItemRarity.RARE),
	EPIC(4, ItemRarity.EPIC),
	LEGENDARY(5, ItemRarity.LEGENDARY);

	private final int code;
	private final ItemRarity itemRarity;

	public static WowheadItemQuality fromCode(int code) {
		return Stream.of(values()).filter(x -> x.code == code).findFirst().orElseThrow();
	}
}
