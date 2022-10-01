package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.SocketType;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Data
@AllArgsConstructor
public class ItemDTO {
	private int id;
	private String name;
	private ItemRarity rarity;
	private ItemType itemType;
	private double score;
	private String source;
	private String attributes;
	private int socketCount;
	private SocketType socket1Color;
	private SocketType socket2Color;
	private SocketType socket3Color;
	private String socketBonus;
}
