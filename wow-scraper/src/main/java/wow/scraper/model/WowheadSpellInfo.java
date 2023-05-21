package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2022-10-26
 */

@Data
@JsonIgnoreProperties({
		"buff",
		"buffspells",
		"completion_category",
		"spells",
		"tooltip2",
})
public class WowheadSpellInfo {
	private String name;
	private int quality;
	private String icon;
	private String tooltip;
}
