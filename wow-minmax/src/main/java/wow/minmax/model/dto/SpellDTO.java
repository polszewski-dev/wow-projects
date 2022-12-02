package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
@Data
@AllArgsConstructor
public class SpellDTO {
	private String name;
	private Integer rank;
	private String icon;
	private String tooltip;
}
