package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2022-12-31
 */
@Data
@AllArgsConstructor
public class SocketBonusStatusDTO {
	private String bonus;
	private boolean enabled;
}
