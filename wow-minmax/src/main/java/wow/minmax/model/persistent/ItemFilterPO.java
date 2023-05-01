package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2023-04-05
 */
@Data
@AllArgsConstructor
public class ItemFilterPO implements Serializable {
	boolean heroics;
	boolean raids;
	boolean worldBosses;
	boolean pvpItems;
	boolean greens;
	boolean legendaries;
}
