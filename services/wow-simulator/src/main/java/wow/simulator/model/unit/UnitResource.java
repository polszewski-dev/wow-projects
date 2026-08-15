package wow.simulator.model.unit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.commons.model.Percent;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.spell.Spell;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static wow.commons.model.spell.ResourceType.HEALTH;

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

	public Percent getPercent() {
		return Percent.of(100.0 * current / max);
	}

	public void set(int current, int max) {
		this.current = min(current, max);
		this.max = max;
	}

	public int increase(int amount, boolean crit, Spell spell, Unit caster) {
		if (amount == 0) {
			return 0;
		}

		assertNonNegative(amount);

		int previous = current;

		this.current = min(current + amount, max);

		int actualAmount = current - previous;

		if (actualAmount > 0) {
			getGameLog().increasedResource(type, spell, owner, actualAmount, current, previous, crit, caster);
		}

		return actualAmount;
	}

	public int decrease(int amount, boolean crit, Spell spell, Unit caster) {
		if (amount == 0) {
			return 0;
		}

		assertNonNegative(amount);

		int previous = current;

		this.current = max(current - amount, 0);

		int actualAmount = previous - current;

		if (actualAmount > 0) {
			getGameLog().decreasedResource(type, spell, owner, actualAmount, current, previous, crit, caster);

			if (current == 0 && type == HEALTH) {
				getGameLog().targetDied(owner, caster);
				owner.triggerDeath(caster);
			}
		}

		return actualAmount;
	}

	public void pay(int amount, Ability ability) {
		if (!canPay(amount)) {
			throw new IllegalArgumentException("Can't pay %s when having only %s".formatted(amount, current));
		}
		decrease(amount, false, ability, owner);
	}

	public boolean canPay(int amount) {
		if (canSpendAll(type)) {
			return amount <= current;
		} else {
			return amount < current;
		}
	}

	private boolean canSpendAll(ResourceType type) {
		return type != HEALTH;
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
