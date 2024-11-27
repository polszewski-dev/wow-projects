package wow.minmax.model.persistent;

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
public class ItemFilterPO {
	boolean heroics;
	boolean raids;
	boolean worldBosses;
	boolean pvpItems;
	boolean greens;
	boolean legendaries;
}
