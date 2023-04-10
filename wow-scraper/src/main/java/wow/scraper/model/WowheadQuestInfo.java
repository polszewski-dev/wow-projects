package wow.scraper.model;

import lombok.Data;
import wow.commons.model.pve.Side;

/**
 * User: POlszewski
 * Date: 2023-04-11
 */
@Data
public class WowheadQuestInfo {
	private String html;
	private Integer requiredLevel;
	private Side requiredSide;
}
