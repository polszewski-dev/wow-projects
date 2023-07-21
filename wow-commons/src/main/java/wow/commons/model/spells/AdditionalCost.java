package wow.commons.model.spells;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
public record AdditionalCost(CostType type, int amount, boolean scaled) {
	public AdditionalCost {
		Objects.requireNonNull(type);
	}
}
