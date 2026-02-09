package wow.minmax.service;

import wow.minmax.model.Player;
import wow.simulator.client.dto.StatsDTO;

/**
 * User: POlszewski
 * Date: 2024-11-09
 */
public interface SimulatorService {
	StatsDTO simulate(Player player);
}
