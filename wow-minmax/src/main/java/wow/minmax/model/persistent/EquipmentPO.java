package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.categorization.ItemSlot;

import java.io.Serializable;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
@AllArgsConstructor
public class EquipmentPO implements Serializable {
	private Map<ItemSlot, EquippableItemPO> itemsBySlot;
}
