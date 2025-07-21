package wow.simulator.model.unit.action;

import wow.simulator.model.time.AnyTime;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public class IdleAction extends UnitAction {
	private final AnyTime end;

	public IdleAction(Unit owner, AnyTime end) {
		super(owner);
		this.end = end;
	}

	@Override
	protected void setUp() {
		on(end, () -> {});
	}

	@Override
	public boolean triggersGcd() {
		return false;
	}
}
