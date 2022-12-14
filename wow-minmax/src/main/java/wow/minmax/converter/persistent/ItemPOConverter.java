package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Item;
import wow.commons.model.pve.Phase;
import wow.commons.repository.ItemRepository;
import wow.minmax.converter.ParametrizedConverter;
import wow.minmax.model.persistent.ItemPO;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class ItemPOConverter extends ParametrizedConverter<Item, ItemPO> {
	private final ItemRepository itemRepository;

	@Override
	protected ItemPO doConvert(Item item, Map<String, Object> params) {
		return new ItemPO(item.getId(), item.getName());
	}

	@Override
	protected Item doConvertBack(ItemPO value, Map<String, Object> params) {
		Phase phase = (Phase)params.get(PlayerProfilePOConverter.PARAM_PHASE);
		return itemRepository.getItem(value.getId(), phase).orElseThrow();
	}
}
