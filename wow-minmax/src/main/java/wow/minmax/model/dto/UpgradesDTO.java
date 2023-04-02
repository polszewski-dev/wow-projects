package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.categorization.ItemSlotGroup;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpgradesDTO {
	private Map<ItemSlotGroup, List<UpgradeDTO>> upgradesBySlotGroup;
}
