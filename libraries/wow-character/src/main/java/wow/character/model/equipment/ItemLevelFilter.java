package wow.character.model.equipment;

import lombok.NoArgsConstructor;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.item.Item;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import static wow.commons.model.categorization.ItemRarity.*;

/**
 * User: POlszewski
 * Date: 2025-04-11
 */
@NoArgsConstructor
public class ItemLevelFilter {
	private final Map<ItemRarity, Integer> minItemLevelByRarity = new EnumMap<>(ItemRarity.class);

	public ItemLevelFilter(Map<ItemRarity, Integer> minItemLevelByRarity) {
		this.minItemLevelByRarity.putAll(minItemLevelByRarity);
	}

	public static ItemLevelFilter everything() {
		return new ItemLevelFilter();
	}

	public static ItemLevelFilter of(int minUncommonILvl, int minRareILvl, int minEpicILvl) {
		var filter = new ItemLevelFilter();
		filter.setMinItemLevel(UNCOMMON, minUncommonILvl);
		filter.setMinItemLevel(RARE, minRareILvl);
		filter.setMinItemLevel(EPIC, minEpicILvl);
		return filter;
	}

	public Map<ItemRarity, Integer> getMinItemLevelByRarity() {
		return Collections.unmodifiableMap(minItemLevelByRarity);
	}

	public int getMinItemLevel(ItemRarity rarity) {
		return minItemLevelByRarity.getOrDefault(rarity, 0);
	}

	public void setMinItemLevel(ItemRarity rarity, int minItemLevel) {
		minItemLevelByRarity.put(rarity, minItemLevel);
	}

	public boolean matchesFilter(Item item) {
		int minItemLevel = getMinItemLevel(item.getRarity());
		return item.getItemLevel() >= minItemLevel;
	}
}
