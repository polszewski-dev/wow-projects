package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2025-04-12
 */
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({ "itemchoices", "itemrewards", "classs", "race", "reqclass", "reqrace", "money", "type", "reprewards", "wflags" })
public class JsonQuestRewardInfo {
	@JsonProperty("category")
	private int category;

	@JsonProperty("category2")
	private int category2;

	@JsonProperty("id")
	private int id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("level")
	private int level;

	@JsonProperty("reqlevel")
	private int reqlevel;

	@JsonProperty("side")
	private int side;

	@JsonProperty("xp")
	private int xp;

	@JsonProperty("popularity")
	private int popularity;
}
