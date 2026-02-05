package wow.simulator.model.unit.action;

import wow.simulator.model.unit.Unit;

import java.util.function.Consumer;

/**
 * User: POlszewski
 * Date: 2026-02-12
 */
public class ImmediateAction extends UnitAction {
	private final Consumer<Unit> action;

	public ImmediateAction(Unit owner, Consumer<Unit> action) {
		super(owner);
		this.action = action;
	}

	@Override
	public boolean triggersGcd() {
		return false;
	}

	@Override
	protected void setUp() {
		action.accept(owner);
	}
}
