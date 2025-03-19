package wow.simulator.client.dto;

import wow.commons.client.dto.PlayerDTO;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
public record SimulationRequestDTO(
		PlayerDTO player,
		double duration,
		RngType rngType
) {
}
