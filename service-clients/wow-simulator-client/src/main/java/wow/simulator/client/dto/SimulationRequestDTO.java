package wow.simulator.client.dto;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
public record SimulationRequestDTO(
		CharacterDTO character,
		EnemyDTO targetEnemy,
		double duration
) {
}
