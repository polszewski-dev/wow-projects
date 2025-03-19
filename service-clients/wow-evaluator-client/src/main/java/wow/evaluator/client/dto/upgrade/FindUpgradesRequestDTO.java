package wow.evaluator.client.dto.upgrade;

import wow.commons.client.dto.PlayerDTO;
import wow.commons.model.categorization.ItemSlotGroup;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
public record FindUpgradesRequestDTO(
		PlayerDTO player,
		ItemSlotGroup slotGroup,
		ItemFilterDTO itemFilter,
		GemFilterDTO gemFilter,
		Set<String> enchantNames,
		int maxUpgrades
) {
}
