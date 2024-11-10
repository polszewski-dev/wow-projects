package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Item;
import wow.commons.repository.item.ItemRepository;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.ItemPO;

import java.util.Map;

import static wow.minmax.converter.persistent.PoConverterParams.getPhaseId;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class ItemPOConverter implements Converter<Item, ItemPO>, ParametrizedBackConverter<Item, ItemPO> {
	private final ItemRepository itemRepository;

	@Override
	public ItemPO doConvert(Item source) {
		return new ItemPO(source.getId(), source.getName());
	}

	@Override
	public Item doConvertBack(ItemPO source, Map<String, Object> params) {
		return itemRepository.getItem(source.getId(), getPhaseId(params)).orElseThrow();
	}
}
