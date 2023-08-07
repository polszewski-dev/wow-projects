package wow.simulator.simulation;

import lombok.Getter;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.time.Time;
import wow.simulator.model.update.UpdateQueue;
import wow.simulator.model.update.Updateable;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Getter
public class Simulation implements SimulationContextSource {
	private final UpdateQueue updateQueue = new UpdateQueue();
	private final SimulationContext simulationContext;

	public Simulation(SimulationContext simulationContext) {
		this.simulationContext = simulationContext;
		shareClock(updateQueue);
	}

	public void add(Updateable updateable) {
		shareSimulationContext(updateable);
		updateQueue.add(updateable);
	}

	public void updateUntil(Time timeUntil) {
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
}
