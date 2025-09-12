package wow.minmax.model.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2023-04-05
 */
@AllArgsConstructor
@Getter
@Setter
public class ItemFilterConfig {
	private boolean heroics;
	private boolean raids;
	private boolean worldBosses;
	private boolean pvpItems;
	private boolean greens;
	private boolean legendaries;
}
