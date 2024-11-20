package wow.simulator.client.dto;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
public record SimulationRequestDTO(
		CharacterDTO player,
		double duration,
		RngType rngType
) {
}
