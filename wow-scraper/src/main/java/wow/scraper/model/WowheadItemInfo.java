package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-26
 */

@Data
@JsonIgnoreProperties({
		"completion_category",
})
public class WowheadItemInfo {
	private String name;
	private int quality;
	private String icon;
	private String tooltip;
	private List<String> spells;
}
