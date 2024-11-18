package wow.simulator.service;

import wow.commons.model.Duration;
import wow.simulator.client.dto.RngType;
import wow.simulator.model.stats.Stats;
import wow.simulator.model.unit.Player;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
public interface SimulatorService {
	Stats simulate(Player player, Duration duration, RngType rngType);
}
