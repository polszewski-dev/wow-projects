package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Data
@AllArgsConstructor
public class EquippableItemPO implements Serializable {
	private ItemPO item;
	private EnchantPO enchant;
	private int socketCount;
	private GemPO gem1;
	private GemPO gem2;
	private GemPO gem3;
}
