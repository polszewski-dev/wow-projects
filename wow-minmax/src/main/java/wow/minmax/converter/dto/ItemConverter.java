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

import static wow.minmax.converter.dto.DtoConverterParams.getPhaseId;

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
	public ItemDTO doConvert(Item source) {
		return new ItemDTO(
				source.getId(),
				source.getName(),
				source.getRarity(),
				source.getItemType(),
				source.getItemSubType(),
				source.getItemLevel(),
				sourceConverter.getSources(source),
				sourceConverter.getDetailedSources(source),
				getStatString(source),
				source.getSocketTypes(),
				source.getSocketBonus().statString(),
				source.getIcon(),
				null
		);
	}

	@Override
	public Item doConvertBack(ItemDTO source, Map<String, Object> params) {
		return itemRepository.getItem(source.getId(), getPhaseId(params)).orElseThrow();
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

		String socketString = item.getSocketSpecification().socketTypes()
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
