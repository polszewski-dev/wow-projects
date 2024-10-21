package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-22
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpgradeDTO {
	private double changePct;
	private List<EquippableItemDTO> itemDifference;
	private List<String> statDifference;
	private List<String> addedAbilities;
	private List<String> removedAbilities;
}
