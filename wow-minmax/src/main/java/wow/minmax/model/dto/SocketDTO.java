package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2022-11-21
 */
@Data
@AllArgsConstructor
public class SocketDTO {
	private GemDTO gem;
	private boolean matching;
}
