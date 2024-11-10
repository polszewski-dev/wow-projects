package wow.commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2023-04-04
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RacialDTO {
	private String name;
	private String attributes;
	private String icon;
	private String tooltip;
}
