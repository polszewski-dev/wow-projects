package wow.simulator.simulation;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Getter
public class Simulation implements SimulationContextSource {
	private final List<Unit> units = new ArrayList<>();
	private final SimulationContext simulationContext;

	private Time timeUntilSimulationEnd;

	public Simulation(SimulationContext simulationContext) {
		this.simulationContext = simulationContext;
		simulationContext.setSimulation(this);
	}

	public void add(Unit unit) {
		shareSimulationContext(unit);
		this.units.add(unit);
	}

	public void add(Action action) {
		shareSimulationContext(action);
		getScheduler().add(action);
	}

	public void updateUntil(Time timeUntil) {
		this.timeUntilSimulationEnd = timeUntil;

		getGameLog().simulationStarted();

		ensureUnitsHaveActions();

		while (!getScheduler().isEmpty()) {
			var nextUpdateTime = getScheduler().getNextUpdateTime().orElseThrow();

			if (nextUpdateTime.after(timeUntil)) {
				break;
			}

			getClock().advanceTo((Time) nextUpdateTime);
			getScheduler().updateAllPresentActions();
		}

		getClock().advanceTo(timeUntil);
		getGameLog().simulationEnded();
	}

	private void ensureUnitsHaveActions() {
		getScheduler().add(Duration.ZERO, () -> {
			for (var unit : units) {
				unit.ensureAction();
			}
		});
	}

	public void addHandler(GameLogHandler handler) {
		shareClock(handler);
		getGameLog().addHandler(handler);
	}

	public void delayedAction(Duration delay, Runnable runnable) {
		if (delay.isZero()) {
			runnable.run();
		} else {
			getScheduler().add(delay, runnable);
		}
	}

	public Duration getRemainingTime() {
		return timeUntilSimulationEnd.subtract(now());
	}

	public List<Unit> getEnemiesOf(Unit unit) {
		return units.stream()
				.filter(x -> Unit.areHostile(x, unit))
				.toList();
	}

	public List<Unit> getFriendsOf(Unit unit) {
		return units.stream()
				.filter(x -> Unit.areFriendly(x, unit))
				.toList();
	}
}
