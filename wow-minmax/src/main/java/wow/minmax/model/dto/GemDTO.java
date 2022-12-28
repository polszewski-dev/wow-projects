package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.item.GemColor;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Data
@AllArgsConstructor
public class GemDTO {
	private int id;
	private String name;
	private GemColor color;
	private ItemRarity rarity;
	private String source;
	private String attributes;
	private String shortName;
	private String icon;
	private String tooltip;
}
