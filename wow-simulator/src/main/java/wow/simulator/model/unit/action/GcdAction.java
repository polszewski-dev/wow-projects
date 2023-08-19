package wow.simulator.model.unit.action;

import wow.commons.model.Duration;
import wow.simulator.model.action.Action;

/**
 * User: POlszewski
 * Date: 2023-08-18
 */
public class GcdAction extends UnitAction {
	private final Duration gcd;
	private final Action sourceAction;

	public GcdAction(Duration gcd, UnitAction sourceAction) {
		super(sourceAction.getOwner());
		this.gcd = gcd;
		this.sourceAction = sourceAction;
	}

	@Override
	protected void setUp() {
		getGameLog().beginGcd(owner, sourceAction);

		fromNowAfter(gcd, () -> getGameLog().endGcd(owner, sourceAction));
	}
}
