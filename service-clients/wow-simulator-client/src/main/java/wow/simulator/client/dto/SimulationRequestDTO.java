package wow.simulator.client.dto;

import wow.commons.client.dto.NonPlayerDTO;
import wow.commons.client.dto.RaidDTO;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
public record SimulationRequestDTO(
		RaidDTO raid,
		NonPlayerDTO target,
		double duration,
		RngType rngType
) {
}
