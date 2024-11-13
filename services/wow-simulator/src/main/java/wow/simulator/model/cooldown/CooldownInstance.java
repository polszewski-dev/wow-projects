package wow.simulator.model.cooldown;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Updateable;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;
import wow.simulator.util.IdGenerator;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-08-19
 */
public class CooldownInstance implements Updateable, SimulationContextSource {
	private static final IdGenerator<CooldownInstanceId> ID_GENERATOR = new IdGenerator<>(CooldownInstanceId::new);

	@Getter
	protected final CooldownInstanceId id = ID_GENERATOR.newId();
	@Getter
	private final Unit owner;
	@Getter
	private final AbilityId abilityId;
	private final Time endTime;
	private final CooldownAction cooldownAction;

	public CooldownInstance(Unit owner, AbilityId abilityId, Duration duration) {
		this.owner = owner;
		this.abilityId = abilityId;
		this.endTime = now().add(duration);
		this.cooldownAction = new CooldownAction(owner.getClock());
	}

	@Override
	public void update() {
		cooldownAction.update();
	}

	@Override
	public Optional<Time> getNextUpdateTime() {
		return cooldownAction.getNextUpdateTime();
	}

	@Override
	public void onAddedToQueue() {
		cooldownAction.start();
		getGameLog().cooldownStarted(this);
	}

	@Override
	public void onRemovedFromQueue() {
		cooldownAction.interrupt();
	}

	public boolean isActive() {
		return getClock().timeInTheFuture(endTime);
	}

	public Duration getRemainingDuration() {
		return endTime.subtract(now()).min(Duration.ZERO);
	}

	private class CooldownAction extends Action {
		public CooldownAction(Clock clock) {
			super(clock);
		}

		@Override
		protected void setUp() {
			on(endTime, () -> {});
		}

		@Override
		protected void onFinished() {
			getGameLog().cooldownExpired(CooldownInstance.this);
		}

		@Override
		protected void onInterrupted() {
			getGameLog().cooldownExpired(CooldownInstance.this);
		}
	}

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}
}
