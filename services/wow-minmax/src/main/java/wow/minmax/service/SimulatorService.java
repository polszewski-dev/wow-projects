package wow.minmax.service;

import wow.character.model.character.PlayerCharacter;
import wow.simulator.client.dto.StatsDTO;

/**
 * User: POlszewski
 * Date: 2024-11-09
 */
public interface SimulatorService {
	StatsDTO simulate(PlayerCharacter player);
}
