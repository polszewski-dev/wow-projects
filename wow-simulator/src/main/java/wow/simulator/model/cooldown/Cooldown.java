package wow.simulator.model.cooldown;

import wow.commons.model.Duration;
import wow.commons.model.spells.SpellId;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Updateable;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-08-19
 */
public class Cooldown implements Updateable, SimulationContextSource {
	private final Unit owner;
	private final SpellId spellId;
	private final Time endTime;
	private final CooldownAction cooldownAction;

	public Cooldown(Unit owner, SpellId spellId, Duration duration) {
		this.owner = owner;
		this.spellId = spellId;
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
		getGameLog().cooldownStarted(owner, spellId);
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
			getGameLog().cooldownExpired(owner, spellId);
		}

		@Override
		protected void onInterrupted() {
			getGameLog().cooldownExpired(owner, spellId);
		}
	}

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}

	public SpellId getSpellId() {
		return spellId;
	}
}
