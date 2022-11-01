package wow.scraper.model;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties({
	"popularity",
})
public class JsonZoneDetails {
	@JsonProperty(value = "id")
	private int id;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "expansion")
	private int expansion;

	@JsonProperty(value = "nplayers")
	private int nplayers;

	@JsonProperty(value = "instance")
	private int instance;

	@JsonProperty(value = "category")
	private int category;

	@JsonProperty(value = "minlevel")
	private int minlevel;

	@JsonProperty(value = "maxlevel")
	private int maxlevel;

	@JsonProperty(value = "reqlevel")
	private int reqlevel;

	@JsonProperty(value = "lfgReqLevel")
	private int lfgReqLevel;

	@JsonProperty(value = "heroicLevel")
	private int heroicLevel;

	@JsonProperty(value = "territory")
	private int territory;
}
