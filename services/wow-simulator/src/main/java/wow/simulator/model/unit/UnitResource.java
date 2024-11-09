package wow.simulator.model.unit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ResourceType;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

/**
 * User: POlszewski
 * Date: 2023-08-12
 */
@RequiredArgsConstructor
@Getter
public class UnitResource implements SimulationContextSource {
	private final ResourceType type;
	private final Unit owner;
	private int current;
	private int max;

	public void set(int current, int max) {
		this.current = current;
		this.max = max;
	}

	public int increase(int amount, boolean crit, Ability ability) {
		if (amount == 0) {
			return 0;
		}

		assertNonNegative(amount);

		int previous = current;
		this.current = Math.min(current + amount, max);
		getGameLog().increasedResource(type, ability, owner, amount, current, previous, crit);
		return current - previous;
	}

	public int decrease(int amount, boolean crit, Ability ability) {
		if (amount == 0) {
			return 0;
		}

		assertNonNegative(amount);

		int previous = current;
		this.current = Math.max(current - amount, 0);
		getGameLog().decreasedResource(type, ability, owner, amount, current, previous, crit);
		return previous - current;
	}

	public void pay(int amount, Ability ability) {
		if (!canPay(amount)) {
			throw new IllegalArgumentException("Can't pay %s when having only %s".formatted(amount, current));
		}
		decrease(amount, false, ability);
	}

	public boolean canPay(int amount) {
		if (canSpendAll(type)) {
			return amount <= current;
		} else {
			return amount < current;
		}
	}

	private boolean canSpendAll(ResourceType type) {
		return type != ResourceType.HEALTH;
	}

	private static void assertNonNegative(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("Unexpected negative value: " + amount);
		}
	}

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}
}
