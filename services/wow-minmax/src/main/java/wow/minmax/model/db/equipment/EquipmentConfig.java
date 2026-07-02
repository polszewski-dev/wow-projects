package wow.minmax.model.db.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.categorization.ItemSlot;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EquipmentConfig {
	private Map<ItemSlot, EquippableItemConfig> itemsBySlot;
}
