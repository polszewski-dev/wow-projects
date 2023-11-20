package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.pve.GameVersionId;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-10-27
 */
@NoArgsConstructor
@Getter
@Setter
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
		"chrclass",
		"reqclass",
		"races",
		"reqrace",
		"talentspec",
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

	@JsonProperty(value = "enchantId")
	private Integer enchantId;

	@JsonProperty(value = "sourceItemIds")
	private Set<Integer> sourceItemIds;

	private GameVersionId reqVersion;

	private WowheadSpellCategory category;

	private Integer maxRank;
}
