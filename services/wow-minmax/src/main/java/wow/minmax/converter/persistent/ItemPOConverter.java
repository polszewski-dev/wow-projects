package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.item.Item;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.ItemRepository;
import wow.minmax.model.persistent.ItemPO;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class ItemPOConverter implements Converter<Item, ItemPO>, ParametrizedBackConverter<Item, ItemPO, PhaseId> {
	private final ItemRepository itemRepository;

	@Override
	public ItemPO doConvert(Item source) {
		return new ItemPO(source.getId(), source.getName());
	}

	@Override
	public Item doConvertBack(ItemPO source, PhaseId phaseId) {
		return itemRepository.getItem(source.getId(), phaseId).orElseThrow();
	}
}
