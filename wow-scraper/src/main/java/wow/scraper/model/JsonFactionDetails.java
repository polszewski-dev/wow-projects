package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import wow.commons.model.pve.GameVersionId;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
@Data
@JsonIgnoreProperties({
	"popularity",
})
public class JsonFactionDetails implements HasRequiredVersion {
	@JsonProperty(value = "id")
	private int id;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "expansion")
	private int expansion;

	@JsonProperty(value = "side")
	private int side;

	@JsonProperty(value = "category")
	private int category;

	@JsonProperty(value = "category2")
	private int category2;

	@JsonProperty(value = "friendshiprep")
	private int friendshiprep;

	private GameVersionId reqVersion;
}
