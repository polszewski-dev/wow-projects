package wow.minmax.client.dto.equipment;

import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.SocketType;
import wow.minmax.client.dto.PhaseDTO;

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
