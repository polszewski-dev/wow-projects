package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-28
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EquipmentOptionsDTO {
	private List<ItemOptionsDTO> itemOptions;
	private List<EnchantOptionsDTO> enchantOptions;
	private List<GemOptionsDTO> gemOptions;
	private boolean editGems;
	private boolean heroics;
}
