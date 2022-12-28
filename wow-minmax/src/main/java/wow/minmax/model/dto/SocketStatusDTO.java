package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.item.SocketType;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
@Data
@AllArgsConstructor
public class SocketStatusDTO {
	private SocketType socketType;
	private boolean matching;
}
