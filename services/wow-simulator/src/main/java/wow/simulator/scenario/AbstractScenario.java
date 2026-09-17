package wow.simulator.scenario;

import wow.character.model.character.Raid;
import wow.commons.model.Duration;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.service.SimulationCallback;
import wow.simulator.service.SimulatorService;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.util.CountdownCounter;

import java.util.List;
import java.util.function.Supplier;

import static wow.simulator.constant.HiddenEffectNames.*;

/**
 * User: POlszewski
 * Date: 2026-09-10
 */
public abstract class AbstractScenario implements Scenario, SimulationCallback {
	private final Supplier<SimulationContext> simulationContextSupplier;
	private final SimulatorService simulatorService;

	protected Raid<Player> raid;
	protected Player main;

	protected AbstractScenario(Supplier<SimulationContext> simulationContextSupplier, SimulatorService simulatorService) {
		this.simulationContextSupplier = simulationContextSupplier;
		this.simulatorService = simulatorService;
	}

	protected void doExecute(Duration duration, List<GameLogHandler> handlers) {
		simulatorService.simulate(raid, getTargets(), duration, simulationContextSupplier.get(), handlers, this);
	}

	protected void doSetUnits(Raid<Player> raid) {
		this.raid = raid;
		this.main = raid.getFirstMember();
	}

	@Override
	public void beforePreparationPhaseStarts() {
		// void
	}

	@Override
	public void afterPreparationPhaseEnds() {
		assignTargets();
		assignFocuses();
		addHiddenEffects();
		setTargetHp();
		endSimulationAfterLastTargetDies();
	}

	protected abstract void assignTargets();

	protected void assignFocuses() {
		raid.forEach(member -> member.setFocus(main));
	}

	private void addHiddenEffects() {
		raid.forEachMemberAndPet((Unit memberOrPet) -> {
			addHiddenEffect(memberOrPet);
		});
	}

	private void addHiddenEffect(Unit memberOrPet) {
		memberOrPet.addHiddenEffect(BONUS_HP5, 5000);
		memberOrPet.addHiddenEffect(BONUS_MP5, 5000);
		memberOrPet.setAllResourcesToMax();
	}

	private void setTargetHp() {
		getTargets().forEach(this::setTargetHp);
	}

	private void setTargetHp(Unit target) {
		target.addHiddenEffect(BONUS_STAMINA, 100_000_000);
		target.setAllResourcesToMax();
	}

	private void endSimulationAfterLastTargetDies() {
		var targets = getTargets();
		var counter = new CountdownCounter(targets.size(), main.getSimulation()::finish);

		targets.forEach(target -> target.setOnDeath(self -> counter.decrease()));
	}

	protected abstract List<Unit> getTargets();
}
