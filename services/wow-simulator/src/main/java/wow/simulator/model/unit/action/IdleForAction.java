package wow.simulator.model.unit.action;

import wow.commons.model.AnyDuration;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public class IdleForAction extends IdleAction {
	private final AnyDuration delay;

	public IdleForAction(Unit owner, AnyDuration delay) {
		super(owner);
		this.delay = delay;
	}

	@Override
	protected void setUp() {
		var end = now().add(delay);

		on(end, () -> {});
	}

	@Override
	public boolean endIsInInfinity() {
		return delay.isInfinite();
	}
}
