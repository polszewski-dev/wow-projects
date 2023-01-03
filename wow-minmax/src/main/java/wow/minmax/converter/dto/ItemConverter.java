package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Item;
import wow.commons.repository.ItemRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.dto.ItemDTO;

import java.util.Map;
import java.util.stream.Collectors;

import static wow.minmax.converter.dto.DtoConverterParams.getPhase;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class ItemConverter implements Converter<Item, ItemDTO>, ParametrizedBackConverter<Item, ItemDTO> {
	private final SourceConverter sourceConverter;
	private final ItemRepository itemRepository;

	@Override
	public ItemDTO doConvert(Item item) {
		return new ItemDTO(
				item.getId(),
				item.getName(),
				item.getRarity(),
				item.getItemType(),
				item.getItemLevel(),
				sourceConverter.getSources(item),
				getStatString(item),
				item.getSocketTypes(),
				item.getSocketBonus().statString(),
				item.getIcon(),
				item.getTooltip()
		);
	}

	@Override
	public Item doConvertBack(ItemDTO value, Map<String, Object> params) {
		return itemRepository.getItem(value.getId(), getPhase(params)).orElseThrow();
	}

	private String getStatString(Item item) {
		return item.getAttributes().statString() +
				getSocketString(item) +
				getItemSetString(item);
	}

	private String getSocketString(Item item) {
		if (!item.hasSockets()) {
			return "";
		}

		String socketString = item.getSocketSpecification().getSocketTypes()
				.stream()
				.map(x -> "[" + x.name().charAt(0) + "]")
				.collect(Collectors.joining());

		return String.format(", %s+%s", socketString, item.getSocketBonus());
	}

	private String getItemSetString(Item item) {
		if (item.getItemSet() == null) {
			return "";
		}

		return String.format(", Set: %s", item.getItemSet().getName());
	}
}
