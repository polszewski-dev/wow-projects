package wow.commons.model.spells;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
public record Cost(CostType type, int amount) {
	public Cost {
		Objects.requireNonNull(type);
	}
}
