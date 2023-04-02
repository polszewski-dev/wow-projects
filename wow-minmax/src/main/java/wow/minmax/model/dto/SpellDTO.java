package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpellDTO {
	private String name;
	private Integer rank;
	private String icon;
	private String tooltip;
}
