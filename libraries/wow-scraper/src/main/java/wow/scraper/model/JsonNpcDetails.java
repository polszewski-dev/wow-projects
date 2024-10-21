package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.pve.GameVersionId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-01
 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({
		"popularity",
		"minlevel",
		"maxlevel",
		"react",
		"family",
		"type",
		"hasQuests"
})
public class JsonNpcDetails implements HasRequiredVersion {
	@JsonProperty(value = "id")
	private int id;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "tag")
	private String tag;

	@JsonProperty(value = "classification")
	private int classification;

	@JsonProperty(value = "boss")
	private int boss;

	private List<Integer> location;

	private GameVersionId reqVersion;
}
