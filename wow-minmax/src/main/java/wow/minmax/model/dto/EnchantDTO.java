package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.categorization.ItemRarity;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnchantDTO {
	private int id;
	private String name;
	private ItemRarity rarity;
	private String attributes;
	private String icon;
	private String tooltip;
}
