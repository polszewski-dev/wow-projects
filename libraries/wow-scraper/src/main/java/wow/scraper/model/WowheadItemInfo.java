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
		"completion_category",
		"spells"
})
public class WowheadItemInfo {
	private String name;
	private int quality;
	private String icon;
	private String tooltip;
}
