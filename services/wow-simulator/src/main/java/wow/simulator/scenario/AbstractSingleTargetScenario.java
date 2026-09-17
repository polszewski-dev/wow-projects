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
 * Date: 2026-09-17
 */
public abstract class AbstractSingleTargetScenario extends AbstractScenario implements SingleTargetScenario {
	protected Unit target;

	protected AbstractSingleTargetScenario(Supplier<SimulationContext> simulationContextSupplier, SimulatorService simulatorService) {
		super(simulationContextSupplier, simulatorService);
	}

	protected void setUnits(Raid<Player> raid, Unit target) {
		doSetUnits(raid);
		this.target = target;
	}

	@Override
	protected void assignTargets() {
		raid.forEach(member -> member.setTarget(target));
	}

	@Override
	protected List<Unit> getTargets() {
		return List.of(target);
	}
}
