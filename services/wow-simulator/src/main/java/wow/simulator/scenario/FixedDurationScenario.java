package wow.simulator.scenario;

import lombok.RequiredArgsConstructor;
import wow.character.model.character.Raid;
import wow.commons.model.Duration;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.simulator.log.handler.GameLogHandler;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;
import wow.simulator.service.SimulatorService;
import wow.simulator.simulation.SimulationContext;

import java.util.List;
import java.util.function.Supplier;

import static wow.commons.model.spell.ResourceType.HEALTH;

/**
 * User: POlszewski
 * Date: 2026-09-14
 */
public class FixedDurationScenario extends AbstractSingleTargetScenario {
	private final Duration duration;

	private boolean targetHpComputed;

	public FixedDurationScenario(Duration duration, Supplier<SimulationContext> simulationContextSupplier, SimulatorService simulatorService) {
		super(simulationContextSupplier, simulatorService);
		this.duration = duration;
	}

	@Override
	public void execute(Raid<Player> raid, Unit target, List<GameLogHandler> handlers) {
		setUnits(raid, target);
		computeTargetHp();
		doExecute(duration, handlers);
	}

	private void computeTargetHp() {
		if (targetHpComputed) {
			return;
		}

		var targetHp = getEstimatedTargetHp();

		targetHpShallBe(targetHp);
		targetHpComputed = true;
	}

	private int getEstimatedTargetHp() {
		var dps = getEstimatedDps(Duration.seconds(60), 1_000_000_000);
		var dps2 = getEstimatedDps(duration, getTargetHp(dps));

		return getTargetHp(dps2);
	}

	private double getEstimatedDps(Duration experimentDuration, int experimentTargetHp) {
		var damageTakenHandler = new DamageTakenHandler(target);

		targetHpShallBe(experimentTargetHp);
		doExecute(experimentDuration, List.of(damageTakenHandler));
		resetUnits();

		var damageTaken = damageTakenHandler.totalDamageTaken;

		return damageTaken / experimentDuration.getSeconds();
	}

	private int getTargetHp(double dps) {
		return (int) (dps * duration.getSeconds());
	}

	@RequiredArgsConstructor
	private static class DamageTakenHandler implements GameLogHandler {
		private final Unit unit;
		private int totalDamageTaken;
		
		@Override
		public void decreasedResource(ResourceType type, Spell spell, Unit target, int amount, boolean direct, boolean crit, Unit caster) {
			if (type == HEALTH && target == unit) {
				totalDamageTaken += amount;
			}
		}
	}
}
