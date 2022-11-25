package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
@AllArgsConstructor
@Getter
public class Cost {
	@NonNull
	private final CostType type;
	private final int amount;
}
