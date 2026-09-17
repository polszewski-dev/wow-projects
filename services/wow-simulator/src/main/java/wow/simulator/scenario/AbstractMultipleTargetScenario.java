package wow.simulator.scenario;

import wow.character.model.character.Raid;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.service.SimulatorService;
import wow.simulator.simulation.SimulationContext;

import java.util.List;
import java.util.function.Supplier;

/**
 * User: POlszewski
 * Date: 2026-09-18
 */
public abstract class AbstractMultipleTargetScenario extends AbstractScenario implements MultipleTargetScenario {
	private List<Unit> targets;

	protected AbstractMultipleTargetScenario(Supplier<SimulationContext> simulationContextSupplier, SimulatorService simulatorService) {
		super(simulationContextSupplier, simulatorService);
	}

	protected void setUnits(Raid<Player> raid, List<Unit> targets) {
		doSetUnits(raid);
		this.targets = targets;
	}

	@Override
	protected void assignTargets() {
		raid.forEach(member -> member.setTarget(targets.getFirst()));
	}

	@Override
	protected List<Unit> getTargets() {
		return targets;
	}
}
