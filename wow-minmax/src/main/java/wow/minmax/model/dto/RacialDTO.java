package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: POlszewski
 * Date: 2023-04-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RacialDTO {
	private String name;
	private String attributes;
	private String icon;
	private String tooltip;
}
