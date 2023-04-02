package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.categorization.ItemSlot;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDTO {
	private Map<ItemSlot, EquippableItemDTO> itemsBySlot;
}
