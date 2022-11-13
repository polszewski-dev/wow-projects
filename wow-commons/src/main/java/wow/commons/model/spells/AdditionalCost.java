package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
@AllArgsConstructor
@Getter
public class AdditionalCost {
	private final CostType type;
	private final int amount;
	private final boolean scaled;
}
