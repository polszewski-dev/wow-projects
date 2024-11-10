package wow.minmax.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.item.SocketType;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SocketStatusDTO {
	private int socketNo;
	private SocketType socketType;
	private boolean matching;
}
