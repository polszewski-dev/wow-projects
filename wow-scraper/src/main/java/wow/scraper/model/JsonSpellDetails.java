package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2022-10-27
 */
@Data
@JsonIgnoreProperties({
		"cat",
		"learnedat",
		"nskillup",
		"rank",
		"schools",
		"skill",
		"source",
		"trainingcost",
		"popularity",
		"colors",
		"reagents",
		"creates",
		"contentPhase",
})
public class JsonSpellDetails implements JsonCommonDetails {
	@JsonProperty(value = "id")
	private Integer id;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "level")
	private Integer level;//Item level

	@JsonProperty(value = "quality")
	private Integer quality;

	@JsonProperty(value = "htmlTooltip")
	private String htmlTooltip;

	@JsonProperty(value = "icon")
	private String icon;
}
