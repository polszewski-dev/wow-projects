package wow.simulator.simulation;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Time;
import wow.simulator.model.update.UpdateQueue;
import wow.simulator.model.update.Updateable;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Getter
public class Simulation implements SimulationContextSource {
	private final UpdateQueue<Updateable> updateQueue = new UpdateQueue<>();
	private final SimulationContext simulationContext;
	private Time timeUntilSimulationEnd;

	public Simulation(SimulationContext simulationContext) {
		this.simulationContext = simulationContext;
		simulationContext.setSimulation(this);
		shareClock(updateQueue);
	}

	public void add(Updateable updateable) {
		shareSimulationContext(updateable);
		updateQueue.add(updateable);
	}

	public void updateUntil(Time timeUntil) {
		this.timeUntilSimulationEnd = timeUntil;

		getGameLog().simulationStarted();

		while (!updateQueue.isEmpty()) {
			Time nextUpdateTime = updateQueue.getNextUpdateTime().orElseThrow();

			if (nextUpdateTime.compareTo(timeUntil) > 0) {
				break;
			}

			getClock().advanceTo(nextUpdateTime);
			updateQueue.updateAllPresentActions();
		}

		getClock().advanceTo(timeUntil);
		getGameLog().simulationEnded();
	}

	public void addHandler(GameLogHandler handler) {
		shareClock(handler);
		getGameLog().addHandler(handler);
	}

	public void delayedAction(Duration delay, Runnable handler) {
		if (delay.isZero()) {
			handler.run();
			return;
		}
		Action action = new Action(getClock()) {
			@Override
			protected void setUp() {
				fromNowAfter(delay, handler);
			}
		};
		updateQueue.add(action);
	}

	public Duration getRemainingTime() {
		return timeUntilSimulationEnd.subtract(now());
	}
}
