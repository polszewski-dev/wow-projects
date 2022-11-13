package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
@AllArgsConstructor
@Getter
public class Cost {
	private final CostType type;
	private final int amount;
}
