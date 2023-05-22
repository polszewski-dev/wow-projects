package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-05-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnchantOptionsDTO {
	private ItemType itemType;
	private ItemSubType itemSubType;
	private List<EnchantDTO> enchants;
}
