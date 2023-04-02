package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuffDTO {
	private int id;
	private String name;
	private String attributes;
	private String icon;
	private String tooltip;
	private boolean enabled;
}
