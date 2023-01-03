package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Item;
import wow.commons.repository.ItemRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.ItemPO;

import java.util.Map;

import static wow.minmax.converter.persistent.PoConverterParams.getPhase;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class ItemPOConverter implements Converter<Item, ItemPO>, ParametrizedBackConverter<Item, ItemPO> {
	private final ItemRepository itemRepository;

	@Override
	public ItemPO doConvert(Item item) {
		return new ItemPO(item.getId(), item.getName());
	}

	@Override
	public Item doConvertBack(ItemPO value, Map<String, Object> params) {
		return itemRepository.getItem(value.getId(), getPhase(params)).orElseThrow();
	}
}
