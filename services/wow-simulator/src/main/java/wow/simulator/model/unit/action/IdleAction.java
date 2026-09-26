package wow.simulator.model.unit.action;

import wow.simulator.model.time.AnyTime;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public abstract class IdleAction extends UnitAction {
	protected IdleAction(Unit owner) {
		super(owner);
	}

	@Override
	public boolean triggersGcd() {
		return false;
	}

	public abstract boolean endIsInInfinity();

	protected void setUpEmptyStep(AnyTime end) {
		on(end, () -> {});
	}
}
