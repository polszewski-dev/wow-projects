package wow.minmax.model.db.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@AllArgsConstructor
@Getter
@Setter
public class EquippableItemConfig {
	private int itemId;
	private Integer enchantId;
	private List<Integer> gemIds;
}
