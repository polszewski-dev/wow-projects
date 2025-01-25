package wow.simulator.simulation;

import wow.character.service.CharacterCalculationService;
import wow.simulator.log.GameLog;
import wow.simulator.model.rng.RngFactory;
import wow.simulator.model.time.Clock;
import wow.simulator.model.update.Scheduler;

/**
 * User: POlszewski
 * Date: 2023-08-09
 */
public interface SimulationContextSource extends TimeSource {
	SimulationContext getSimulationContext();

	@Override
	default Clock getClock() {
		return getSimulationContext().getClock();
	}

	default GameLog getGameLog() {
		return getSimulationContext().getGameLog();
	}

	default CharacterCalculationService getCharacterCalculationService() {
		return getSimulationContext().getCharacterCalculationService();
	}

	default RngFactory getRngFactory() {
		return getSimulationContext().getRngFactory();
	}

	default Simulation getSimulation() { return getSimulationContext().getSimulation(); }

	default Scheduler getScheduler() { return getSimulationContext().getScheduler(); }

	default void shareSimulationContext(SimulationContextAware simulationContextAware) {
		simulationContextAware.setSimulationContext(getSimulationContext());
	}

	default void shareSimulationContext(Object object) {
		if (object instanceof SimulationContextAware simulationContextAware) {
			shareSimulationContext(simulationContextAware);
		}
	}

	default void shareClock(TimeAware timeAware) {
		timeAware.setClock(getClock());
	}

	default void shareClock(Object object) {
		if (object instanceof TimeAware timeAware) {
			shareClock(timeAware);
		}
	}
}
