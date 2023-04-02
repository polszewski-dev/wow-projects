package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpgradeDTO {
	private double changePct;
	private List<EquippableItemDTO> itemDifference;
	private List<String> statDifference;
	private List<String> addedAbilities;
	private List<String> removedAbilities;
}
