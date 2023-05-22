package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentOptionsDTO {
	private List<ItemOptionsDTO> itemOptions;
	private List<EnchantOptionsDTO> enchantOptions;
	private List<GemOptionsDTO> gemOptions;
	private boolean editGems;
	private boolean heroics;
}
