package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
	private List<GemPO> gems;
}
