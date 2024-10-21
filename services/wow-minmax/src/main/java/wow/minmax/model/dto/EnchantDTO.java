package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.categorization.ItemRarity;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EnchantDTO {
	private int id;
	private String name;
	private ItemRarity rarity;
	private String icon;
	private String tooltip;
}
