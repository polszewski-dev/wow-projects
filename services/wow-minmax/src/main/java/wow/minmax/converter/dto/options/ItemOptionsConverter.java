package wow.minmax.converter.dto.options;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.equipment.ItemConverter;
import wow.minmax.client.dto.options.ItemOptionsDTO;
import wow.minmax.model.options.ItemOptions;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Component
@AllArgsConstructor
public class ItemOptionsConverter implements Converter<ItemOptions, ItemOptionsDTO> {
	private final ItemConverter itemConverter;

	@Override
	public ItemOptionsDTO doConvert(ItemOptions source) {
		return new ItemOptionsDTO(
				source.itemSlot(),
				itemConverter.convertList(source.items())
		);
	}
}
