package wow.simulator.simulation;

import wow.commons.model.Duration;
import wow.simulator.model.action.Action;
import wow.simulator.model.unit.Unit;

/**
 * User: POlszewski
 * Date: 2025-10-13
 */
class RegenAction extends Action {
	private final Simulation simulation;

	private static final Duration TICK_INTERVAL = Duration.seconds(2);

	public RegenAction(Simulation simulation) {
		super(simulation.getClock());
		this.simulation = simulation;
	}

	@Override
	protected void setUp() {
		fromNowAfter(TICK_INTERVAL, this::tick);
	}

	private void tick() {
		simulation.forEachUnit(this::regen);
		fromNowAfter(TICK_INTERVAL, this::tick);
	}

	private void regen(Unit unit) {
		unit.regen(TICK_INTERVAL);
	}
}
