package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Data
@AllArgsConstructor
public class EquippableItemDTO {
	private ItemDTO item;
	private EnchantDTO enchant;
	private GemDTO gem1;
	private GemDTO gem2;
	private GemDTO gem3;
	private boolean socket1Matching;
	private boolean socket2Matching;
	private boolean socket3Matching;
	private boolean socketBonusEnabled;
	private ItemOptionsDTO itemOptions;
}
