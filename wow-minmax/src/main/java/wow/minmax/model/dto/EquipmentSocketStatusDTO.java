package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.categorization.ItemSlot;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
@Data
@AllArgsConstructor
public class EquipmentSocketStatusDTO {
	private Map<ItemSlot, ItemSocketStatusDTO> socketStatusesByItemSlot;
}
