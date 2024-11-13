package wow.simulator.model.cooldown;

import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Handle;
import wow.simulator.model.update.UpdateQueue;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;
import wow.simulator.simulation.TimeAware;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-08-19
 */
public class Cooldowns implements SimulationContextSource, TimeAware {
	private final Unit owner;
	private final UpdateQueue<CooldownInstance> updateQueue = new UpdateQueue<>();

	public Cooldowns(Unit owner) {
		this.owner = owner;
	}

	public void updateAllPresentCooldowns() {
		updateQueue.updateAllPresentActions();
	}

	public Optional<Time> getNextUpdateTime() {
		return updateQueue.getNextUpdateTime();
	}

	public void triggerCooldown(Ability ability, Duration actualDuration) {
		if (actualDuration.isZero()) {
			return;
		}

		if (isOnCooldown(ability.getAbilityId())) {
			throw new IllegalStateException();
		}

		var cooldown = new CooldownInstance(owner, ability.getAbilityId(), actualDuration);

		updateQueue.add(cooldown);
	}

	public boolean isOnCooldown(AbilityId abilityId) {
		var cooldown = getCooldown(abilityId);
		return cooldown.isPresent() && cooldown.get().isActive();
	}

	private Optional<CooldownInstance> getCooldown(AbilityId abilityId) {
		return updateQueue.getElements().stream()
				.map(Handle::get)
				.filter(x -> x.getAbilityId() == abilityId)
				.findAny();
	}

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}

	@Override
	public void setClock(Clock clock) {
		updateQueue.setClock(clock);
	}
}
