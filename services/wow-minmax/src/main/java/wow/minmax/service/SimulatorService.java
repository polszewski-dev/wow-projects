package wow.minmax.service;

import wow.minmax.model.CharacterId;
import wow.simulator.client.dto.SimulationResponseDTO;

/**
 * User: POlszewski
 * Date: 2024-11-09
 */
public interface SimulatorService {
	SimulationResponseDTO simulate(CharacterId characterId);
}
