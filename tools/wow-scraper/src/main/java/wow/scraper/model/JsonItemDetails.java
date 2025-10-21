package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-27
 */
@NoArgsConstructor
@Getter
@Setter
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
		"skill",
		"subsubclass",
		"nslots",
		"dmgrange",
		"namedesc"
})
public class JsonItemDetails implements JsonCommonDetails {
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

	@JsonProperty(value = "htmlTooltip")
	private String htmlTooltip;

	@JsonProperty(value = "icon")
	private String icon;

	private String seasonId;

	private String phaseId;

	private WowheadItemCategory category;

	public List<JsonSourceMore> getSourcesOf(WowheadSource source) {
		List<JsonSourceMore> result = new ArrayList<>();

		for (int i = 0; i < sources.size(); i++) {
			if (sources.get(i) == source.getCode() && sourceMores != null && !sourceMores.isEmpty()) {
				result.add(sourceMores.get(i));
			}
		}

		return result;
	}
}
