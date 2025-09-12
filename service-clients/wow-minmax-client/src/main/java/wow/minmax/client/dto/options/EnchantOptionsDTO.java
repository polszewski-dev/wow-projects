package wow.minmax.client.dto.options;

import wow.commons.client.dto.equipment.EnchantDTO;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-05-23
 */
public record EnchantOptionsDTO(
		ItemType itemType,
		ItemSubType itemSubType,
		List<EnchantDTO> enchants
) {
}
