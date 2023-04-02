package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.SocketType;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentOptionsDTO {
	private Map<ItemSlot, List<ItemDTO>> itemsByItemSlot;
	private Map<ItemType, List<EnchantDTO>> enchantsByItemType;
	private Map<SocketType, List<GemDTO>> gemsBySocketType;
}
