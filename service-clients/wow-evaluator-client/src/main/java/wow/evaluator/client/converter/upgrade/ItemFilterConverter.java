package wow.evaluator.client.converter.upgrade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.ItemFilter;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.Converter;
import wow.evaluator.client.dto.upgrade.ItemFilterDTO;

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
				source.heroics(),
				source.raids(),
				source.worldBosses(),
				source.pvpItems(),
				source.greens(),
				source.legendaries()
		);
	}
}
