package wow.minmax.client.dto.equipment;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-29
 */
public record ItemSocketStatusDTO(
		List<SocketStatusDTO> socketStatuses,
		SocketBonusStatusDTO socketBonusStatus
) {
}
