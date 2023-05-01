package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: POlszewski
 * Date: 2023-04-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemFilterDTO {
	boolean heroics;
	boolean raids;
	boolean worldBosses;
	boolean pvpItems;
	boolean greens;
	boolean legendaries;
}
