package wow.simulator.simulation;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.spell.EffectReplacementMode;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.action.Action;
import wow.simulator.model.effect.impl.PeriodicEffectInstance;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public class Simulation implements SimulationContextSource {
	private final List<Unit> units = new ArrayList<>();
	@Getter
	private final SimulationContext simulationContext;

	private boolean started;
	@Getter
	private boolean finished;
	private Time timeUntilSimulationEnd;

	private final GroundEffects groundEffects;

	public Simulation(SimulationContext simulationContext) {
		this.simulationContext = simulationContext;
		this.groundEffects = new GroundEffects(simulationContext);
		simulationContext.setSimulation(this);
	}

	public void add(Unit unit) {
		shareSimulationContext(unit);
		this.units.add(unit);
		unit.onAddedToSimulation();
	}

	public void add(Action action) {
		shareSimulationContext(action);
		getScheduler().add(action);
	}

	public void addGroundEffect(PeriodicEffectInstance effect, EffectReplacementMode replacementMode) {
		groundEffects.addEffect(effect, replacementMode);
	}

	public void updateUntil(Time timeUntil) {
		this.timeUntilSimulationEnd = timeUntil;

		start();

		while (!getScheduler().isEmpty()) {
			var nextUpdateTime = getScheduler().getNextUpdateTime().orElseThrow();

			if (nextUpdateTime.after(timeUntil)) {
				break;
			}

			getClock().advanceTo((Time) nextUpdateTime);
			getScheduler().updateAllPresentActions();
		}

		getClock().advanceTo(timeUntil);
	}

	public void start() {
		if (started) {
			return;
		}

		started = true;
		getGameLog().simulationStarted();
		startRegenAction();
		ensureUnitsHaveActions();
	}

	public void finish() {
		if (finished) {
			return;
		}

		finished = true;
		getScheduler().interruptUnfinishedActions();
		getGameLog().simulationEnded();
	}

	private void startRegenAction() {
		add(new RegenAction(this));
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

	public void forEachUnit(Consumer<Unit> action) {
		units.forEach(action);
	}
}
