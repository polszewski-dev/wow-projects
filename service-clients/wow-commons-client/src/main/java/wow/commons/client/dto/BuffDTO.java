package wow.commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.buff.BuffId;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuffDTO {
	private BuffId buffId;
	private int rank;
	private String name;
	private String attributes;
	private String icon;
	private String tooltip;
	private boolean enabled;
}
