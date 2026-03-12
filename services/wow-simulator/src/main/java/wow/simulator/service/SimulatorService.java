package wow.simulator.service;

import wow.character.model.character.Raid;
import wow.commons.model.Duration;
import wow.simulator.client.dto.RngType;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.SimulationContext;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
public interface SimulatorService {
	void simulate(Raid<Player> raid, Unit target, Duration duration, RngType rngType, List<GameLogHandler> handlers);

	void simulate(Raid<Player> raid, Unit target,  Duration duration, SimulationContext simulationContext, List<GameLogHandler> handlers);
}
