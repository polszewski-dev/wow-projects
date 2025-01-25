package wow.simulator.model.cooldown;

import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.CooldownId;
import wow.simulator.model.action.Action;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static wow.commons.model.spell.GcdCooldownId.GCD;

/**
 * User: POlszewski
 * Date: 2023-08-19
 */
public class Cooldowns implements SimulationContextSource {
	private final Unit owner;

	private final Map<CooldownId, CooldownInstance> cooldownsById = new HashMap<>();

	public Cooldowns(Unit owner) {
		this.owner = owner;
	}

	public void triggerCooldown(Ability ability, Duration actualDuration, UnitAction currentAction) {
		var cooldownId = CooldownId.of(ability.getAbilityId());

		triggerCooldown(cooldownId, actualDuration, currentAction);
	}

	public void triggerCooldown(CooldownId cooldownId, Duration actualDuration, UnitAction currentAction) {
		if (actualDuration.isZero()) {
			return;
		}

		if (isOnCooldown(cooldownId)) {
			throw new IllegalStateException();
		}

		var cooldown = new CooldownInstance(cooldownId, owner, actualDuration, currentAction);

		attach(cooldown);
		getScheduler().add(cooldown);
	}

	public void interruptGcd() {
		var gcd = getCooldown(GCD);

		gcd.ifPresent(Action::interrupt);
	}

	public boolean isOnCooldown(AbilityId abilityId) {
		var cooldownId = CooldownId.of(abilityId);

		return isOnCooldown(cooldownId);
	}

	public boolean isOnCooldown(CooldownId cooldownId) {
		var cooldown = getCooldown(cooldownId);

		return cooldown.isPresent() && cooldown.get().isActive();
	}

	private Optional<CooldownInstance> getCooldown(CooldownId cooldownId) {
		return Optional.ofNullable(cooldownsById.get(cooldownId));
	}

	private void attach(CooldownInstance cooldown) {
		cooldownsById.put(cooldown.getCooldownId(), cooldown);
	}

	public void detach(CooldownInstance cooldown) {
		var existingCooldown = cooldownsById.get(cooldown.getCooldownId());

		if (existingCooldown == cooldown) {
			cooldownsById.remove(cooldown.getCooldownId());
		}
	}

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}
}
