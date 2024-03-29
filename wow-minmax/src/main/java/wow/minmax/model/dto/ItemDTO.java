package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.categorization.ItemRarity;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.SocketType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemDTO {
	private int id;
	private String name;
	private ItemRarity rarity;
	private ItemType itemType;
	private ItemSubType itemSubType;
	private double score;
	private String source;
	private String detailedSource;
	private List<SocketType> socketTypes;
	private String socketBonus;
	private String icon;
	private String tooltip;
	private String shortTooltip;
	private PhaseDTO firstAppearedInPhase;
}
