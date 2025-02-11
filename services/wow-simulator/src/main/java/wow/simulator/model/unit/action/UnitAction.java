package wow.simulator.model.unit.action;

import lombok.Getter;
import wow.simulator.model.action.Action;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.impl.UnitImpl;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

/**
 * User: POlszewski
 * Date: 2023-08-12
 */
public abstract class UnitAction extends Action implements SimulationContextSource {
	@Getter
	protected final Unit owner;

	protected UnitAction(Unit owner) {
		super(owner.getClock());
		this.owner = owner;
	}

	@Override
	protected void onFinished() {
		super.onFinished();
		((UnitImpl) owner).actionTerminated(this);
	}

	@Override
	protected void onInterrupted() {
		super.onInterrupted();
		((UnitImpl) owner).actionTerminated(this);
	}

	public abstract boolean triggersGcd();

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}
}
