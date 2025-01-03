package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.commons.model.categorization.ItemSlot;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@AllArgsConstructor
@Getter
@Setter
public class EquipmentPO {
	private Map<ItemSlot, EquippableItemPO> itemsBySlot;
}
