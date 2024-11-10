package wow.commons.client.dto;

import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.SocketType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
public record ItemDTO(
		int id,
		String name,
		ItemRarity rarity,
		ItemType itemType,
		ItemSubType itemSubType,
		double score,
		String source,
		String detailedSource,
		List<SocketType> socketTypes,
		String socketBonus,
		String icon,
		String tooltip,
		String shortTooltip,
		PhaseDTO firstAppearedInPhase
) {
}
