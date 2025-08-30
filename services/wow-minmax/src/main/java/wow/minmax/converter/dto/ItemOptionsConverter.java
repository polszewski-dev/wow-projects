package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ItemConverter;
import wow.minmax.client.dto.ItemOptionsDTO;
import wow.minmax.model.ItemOptions;

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
