package wow.scraper.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonItemDetailsAndTooltip {
	@JsonProperty(value = "details")
	private JsonItemDetails details;

	@JsonProperty(value = "htmlTooltip")
	private String htmlTooltip;

	@JsonProperty(value = "icon")
	private String icon;
}
