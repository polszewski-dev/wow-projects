package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.categorization.ItemSlot;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentSocketStatusDTO {
	private Map<ItemSlot, ItemSocketStatusDTO> socketStatusesByItemSlot;
}
