package wow.minmax.converter.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.item.Item;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.ItemRepository;
import wow.minmax.model.ItemConfig;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class ItemConfigConverter implements Converter<Item, ItemConfig>, ParametrizedBackConverter<Item, ItemConfig, PhaseId> {
	private final ItemRepository itemRepository;

	@Override
	public ItemConfig doConvert(Item source) {
		return new ItemConfig(source.getId(), source.getName());
	}

	@Override
	public Item doConvertBack(ItemConfig source, PhaseId phaseId) {
		return itemRepository.getItem(source.getId(), phaseId).orElseThrow();
	}
}
