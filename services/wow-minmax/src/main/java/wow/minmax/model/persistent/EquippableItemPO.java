package wow.minmax.model.persistent;

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
public class EquippableItemPO {
	private ItemPO item;
	private EnchantPO enchant;
	private List<GemPO> gems;
}
