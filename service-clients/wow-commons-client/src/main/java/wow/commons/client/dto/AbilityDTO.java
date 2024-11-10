package wow.commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AbilityDTO {
	private String name;
	private Integer rank;
	private String icon;
	private String tooltip;
}
