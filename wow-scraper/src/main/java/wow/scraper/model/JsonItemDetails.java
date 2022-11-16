package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-27
 */
@Data
@JsonIgnoreProperties({
		"appearances",
		"armor",
		"armorbonus",
		"attainable",
		"classs",
		"commondrop",
		"contentPhase",
		"displayid",
		"dps",
		"firstseenpatch",
		"flags2",
		"modelviewer",
		"popularity",
		"reqclass",
		"reqlevel",
		"reqrace",
		"slot",
		"slotbak",
		"speed",
		"statsInfo",
		"subclass",
		"arcres",
		"avgbuyout",
		"firres",
		"frores",
		"holres",
		"natres",
		"shares",
		"buyprice",
		"sellprice",
		"spldmg",
		"splheal",
		"splhastertng",
		"str",
		"agi",
		"sta",
		"int",
		"spi",
		"mlecritstrkrtng",
		"rgdcritstrkrtng",
		"mlehitrtng",
		"rgdhitrtng",
		"splcritstrkrtng",
		"splpen",
		"defrtng",
		"manargn",
		"mleatkpwr",
		"rgdatkpwr",
		"dodgertng",
		"parryrtng",
		"resirtng",
		"dmg",
		"maxcount",
		"splhitrtng",
		"reqskill",
		"reqskillrank",
		"classes",
})
public class JsonItemDetails {
	@JsonProperty(value = "id")
	private Integer id;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "level")
	private Integer level;//Item level

	@JsonProperty(value = "side")
	private Integer side;

	@JsonProperty(value = "quality")
	private Integer quality;

	@JsonProperty(value = "source")
	private List<Integer> sources;

	@JsonProperty(value = "sourcemore")
	private List<JsonSourceMore> sourceMores;
}
