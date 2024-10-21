package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-05-23
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EnchantOptionsDTO {
	private ItemType itemType;
	private ItemSubType itemSubType;
	private List<EnchantDTO> enchants;
}
