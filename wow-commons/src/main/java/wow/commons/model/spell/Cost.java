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
		Percent coeff
) {
	public Cost {
		Objects.requireNonNull(resourceType);
		Objects.requireNonNull(baseStatPct);
		Objects.requireNonNull(coeff);
	}

	public Cost(ResourceType resourceType, int amount) {
		this(resourceType, amount, Percent.ZERO, Percent.ZERO);
	}
}
