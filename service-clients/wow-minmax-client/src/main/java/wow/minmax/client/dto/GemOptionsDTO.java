package wow.minmax.client.dto;

import wow.commons.client.dto.GemDTO;
import wow.commons.model.item.SocketType;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-05-23
 */
public record GemOptionsDTO(
		SocketType socketType,
		List<GemDTO> gems
) {
}
