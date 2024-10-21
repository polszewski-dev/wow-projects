package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.item.GemColor;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GemDTO {
	private int id;
	private String name;
	private GemColor color;
	private ItemRarity rarity;
	private String source;
	private String shortName;
	private String icon;
	private String tooltip;
}
