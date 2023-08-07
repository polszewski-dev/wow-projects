package wow.commons.model.spells;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
public record AdditionalCost(ResourceType resourceType, int amount, boolean scaled) {
	public AdditionalCost {
		Objects.requireNonNull(resourceType);
	}
}
