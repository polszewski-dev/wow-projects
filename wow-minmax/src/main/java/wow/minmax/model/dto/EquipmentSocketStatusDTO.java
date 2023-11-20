package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.categorization.ItemSlot;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EquipmentSocketStatusDTO {
	private Map<ItemSlot, ItemSocketStatusDTO> socketStatusesByItemSlot;
}
