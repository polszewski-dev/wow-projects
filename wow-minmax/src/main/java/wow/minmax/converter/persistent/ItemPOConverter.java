package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Item;
import wow.commons.repository.ItemDataRepository;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.ItemPO;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class ItemPOConverter extends Converter<Item, ItemPO> {
	private final ItemDataRepository itemDataRepository;

	@Override
	protected ItemPO doConvert(Item item) {
		return new ItemPO(item.getId(), item.getName());
	}

	@Override
	protected Item doConvertBack(ItemPO value) {
		return itemDataRepository.getItem(value.getId()).orElseThrow();
	}
}
