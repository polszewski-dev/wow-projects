package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2022-10-26
 */
@NoArgsConstructor
@Getter
@Setter
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
