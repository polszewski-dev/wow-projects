package wow.simulator.model.unit.action;

import wow.simulator.model.action.Action;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

/**
 * User: POlszewski
 * Date: 2023-08-12
 */
public abstract class UnitAction extends Action implements SimulationContextSource {
	protected final Unit owner;

	protected UnitAction(Unit owner) {
		super(owner.getClock());
		this.owner = owner;
	}

	public Unit getOwner() {
		return owner;
	}

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}
}
