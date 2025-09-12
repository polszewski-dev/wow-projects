package wow.minmax.client.dto.options;

import wow.commons.model.item.SocketType;
import wow.minmax.client.dto.equipment.GemDTO;

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
