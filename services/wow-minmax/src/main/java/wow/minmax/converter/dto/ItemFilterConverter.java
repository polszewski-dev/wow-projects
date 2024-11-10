package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.ItemFilter;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.ItemFilterDTO;

/**
 * User: POlszewski
 * Date: 2023-04-05
 */
@Component
@AllArgsConstructor
public class ItemFilterConverter implements Converter<ItemFilter, ItemFilterDTO>, BackConverter<ItemFilter, ItemFilterDTO> {
	@Override
	public ItemFilterDTO doConvert(ItemFilter source) {
		return new ItemFilterDTO(
				source.isHeroics(),
				source.isRaids(),
				source.isWorldBosses(),
				source.isPvpItems(),
				source.isGreens(),
				source.isLegendaries()
		);
	}

	@Override
	public ItemFilter doConvertBack(ItemFilterDTO source) {
		return new ItemFilter(
				source.isHeroics(),
				source.isRaids(),
				source.isWorldBosses(),
				source.isPvpItems(),
				source.isGreens(),
				source.isLegendaries()
		);
	}
}
