package wow.simulator.model.unit.action;

import wow.simulator.model.time.AnyTime;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public class IdleUntilAction extends IdleAction {
	private final AnyTime end;

	public IdleUntilAction(Unit owner, AnyTime end) {
		super(owner);
		this.end = end;
	}

	@Override
	protected void setUp() {
		setUpEmptyStep(end);
	}

	@Override
	public boolean endIsInInfinity() {
		return end.isInInfinity();
	}
}
