package wow.scraper.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.pve.Side;

/**
 * User: POlszewski
 * Date: 2023-04-11
 */
@NoArgsConstructor
@Getter
@Setter
public class WowheadQuestInfo {
	private String html;
	private Integer requiredLevel;
	private Side requiredSide;
}
