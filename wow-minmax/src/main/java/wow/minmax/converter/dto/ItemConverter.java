package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Item;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.ItemDTO;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class ItemConverter extends Converter<Item, ItemDTO> {
	private final SourceConverter sourceConverter;

	@Override
	protected ItemDTO doConvert(Item item) {
		return new ItemDTO(
				item.getId(),
				item.getName(),
				item.getRarity(),
				item.getItemType(),
				item.getItemLevel(),
				sourceConverter.getSources(item),
				item.getAttributes().statString(),
				item.getSocketTypes(),
				item.getSocketBonus().statString(),
				item.getIcon(),
				item.getTooltip()
		);
	}
}
