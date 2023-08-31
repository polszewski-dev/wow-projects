package wow.commons.model.spell;

import wow.commons.model.Percent;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
public record Cost(
		ResourceType resourceType,
		int amount,
		Percent baseStatPct,
		Coefficient coefficient,
		Reagent reagent
) {
	public static final Cost NONE = new Cost(ResourceType.MANA, 0, Percent.ZERO, Coefficient.NONE, null);

	public Cost {
		Objects.requireNonNull(resourceType);
		Objects.requireNonNull(baseStatPct);
		Objects.requireNonNull(coefficient);
	}

	public Cost(ResourceType resourceType, int amount) {
		this(resourceType, amount, Percent.ZERO, Coefficient.NONE, null);
	}
}
