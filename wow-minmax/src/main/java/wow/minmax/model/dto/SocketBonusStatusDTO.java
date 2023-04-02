package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: POlszewski
 * Date: 2022-12-31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocketBonusStatusDTO {
	private String bonus;
	private boolean enabled;
}
