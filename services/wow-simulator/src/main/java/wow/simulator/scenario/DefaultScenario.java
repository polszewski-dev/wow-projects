package wow.simulator.scenario;

import wow.character.model.character.Raid;
import wow.commons.model.Duration;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.service.SimulationCallback;
import wow.simulator.service.SimulatorService;
import wow.simulator.simulation.SimulationContext;

import java.util.List;
import java.util.function.Supplier;

import static wow.simulator.constant.HiddenEffectNames.*;

/**
 * User: POlszewski
 * Date: 2026-09-10
 */
public abstract class DefaultScenario implements Scenario, SimulationCallback {
	private final Supplier<SimulationContext> simulationContextSupplier;
	private final SimulatorService simulatorService;

	private Raid<Player> raid;
	private Player main;
	private Unit target;

	protected DefaultScenario(Supplier<SimulationContext> simulationContextSupplier, SimulatorService simulatorService) {
		this.simulationContextSupplier = simulationContextSupplier;
		this.simulatorService = simulatorService;
	}

	protected void doExecute(Duration duration, List<GameLogHandler> handlers) {
		simulatorService.simulate(main.getRaid(), target, duration, simulationContextSupplier.get(), handlers, this);
	}

	protected void setUnits(Raid<Player> raid, Unit target) {
		this.raid = raid;
		this.main = raid.getFirstMember();
		this.target = target;
	}

	@Override
	public void beforePreparationPhaseStarts() {
		raid.forEach(member -> member.setTarget(target));
	}

	@Override
	public void afterPreparationPhaseEnds() {
		raid.forEach(member -> {
			member.setTarget(target);
			member.setFocus(main);
		});

		raid.forEachMemberAndPet((Unit memberOrPet) -> {
			memberOrPet.addHiddenEffect(BONUS_HP5, 5000);
			memberOrPet.addHiddenEffect(BONUS_MP5, 5000);
			memberOrPet.setAllResourcesToMax();
		});

		target.addHiddenEffect(BONUS_STAMINA, 100_000_000);
		target.setAllResourcesToMax();
	}
}
