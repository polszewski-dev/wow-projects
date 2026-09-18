package wow.simulator.scenario;

import wow.character.model.character.Raid;
import wow.commons.model.Duration;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.service.SimulatorService;
import wow.simulator.simulation.SimulationContext;

import java.util.List;
import java.util.function.Supplier;

/**
 * User: POlszewski
 * Date: 2026-09-11
 */
public class InfiniteTargetHealthScenario extends AbstractScenario {
	private final Duration duration;

	public InfiniteTargetHealthScenario(Duration duration, Supplier<SimulationContext> simulationContextSupplier, SimulatorService simulatorService) {
		super(simulationContextSupplier, simulatorService);
		this.duration = duration;
	}

	@Override
	public void execute(Raid<Player> raid, Unit target, List<GameLogHandler> handlers) {
		setUnits(raid, target);
		doExecute(duration, handlers);
	}
}
