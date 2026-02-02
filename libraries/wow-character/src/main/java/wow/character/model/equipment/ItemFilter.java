package wow.character.model.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSource;

import static wow.commons.model.categorization.ItemRarity.LEGENDARY;
import static wow.commons.model.categorization.ItemRarity.UNCOMMON;

/**
 * User: POlszewski
 * Date: 2023-04-05
 */
@Getter
@Setter
@AllArgsConstructor
public class ItemFilter {
	boolean heroics;
	boolean raids;
	boolean worldBosses;
	boolean pvpItems;
	boolean greens;
	boolean legendaries;

	public static ItemFilter everything() {
		return new ItemFilter(
				true, true, true, true, true, true
		);
	}

	public static ItemFilter empty() {
		return new ItemFilter(
				false, false, false, false, false, false
		);
	}

	public boolean matchesFilter(Item item) {
		if (!heroics && item.allSources(ItemSource::isHeroicDrop)) {
			return false;
		}
		if (!raids && item.allSources(ItemSource::isRaidDrop)) {
			return false;
		}
		if (!worldBosses && item.allSources(ItemSource::isWorldBossDrop)) {
			return false;
		}
		if (!pvpItems && item.allSources(ItemSource::isPvP)) {
			return false;
		}
		if (!greens && item.getRarity() == UNCOMMON) {
			return false;
		}
		return legendaries || item.getRarity() != LEGENDARY;
	}
}
