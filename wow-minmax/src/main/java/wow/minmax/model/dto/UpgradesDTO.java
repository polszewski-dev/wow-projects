package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.categorization.ItemSlotGroup;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-22
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpgradesDTO {
	private Map<ItemSlotGroup, List<UpgradeDTO>> upgradesBySlotGroup;
}
