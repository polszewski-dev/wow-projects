package wow.commons.model.spells;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
public record Cost(ResourceType resourceType, int amount) {
	public Cost {
		Objects.requireNonNull(resourceType);
	}
}
