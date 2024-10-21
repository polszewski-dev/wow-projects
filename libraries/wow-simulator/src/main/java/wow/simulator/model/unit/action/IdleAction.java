package wow.simulator.model.unit.action;

import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public class IdleAction extends UnitAction {
	private final Time end;

	public IdleAction(Unit owner, Time end) {
		super(owner);
		this.end = end;
	}

	@Override
	protected void setUp() {
		on(end, () -> {});
	}
}
