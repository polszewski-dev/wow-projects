package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
@Data
@JsonIgnoreProperties({
		"popularity",
		"minlevel",
		"maxlevel",
		"react",
		"type",
		"classification"
})
public class JsonBossDetails {
	@JsonProperty(value = "id")
	private int id;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "tag")
	private String tag;

	@JsonProperty(value = "boss")
	private int boss;

	@JsonProperty(value = "location")
	private List<Integer> location;
}
