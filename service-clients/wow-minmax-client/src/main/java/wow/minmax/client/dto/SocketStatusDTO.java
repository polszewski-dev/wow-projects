package wow.minmax.client.dto;

import wow.commons.model.item.SocketType;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
public record SocketStatusDTO(
		int socketNo,
		SocketType socketType,
		boolean matching
) {
}
