package wow.minmax.converter.db.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.ItemFilter;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.Converter;
import wow.minmax.model.db.equipment.ItemFilterConfig;

/**
 * User: POlszewski
 * Date: 2023-04-05
 */
@Component
@AllArgsConstructor
public class ItemFilterConfigConverter implements Converter<ItemFilter, ItemFilterConfig>, BackConverter<ItemFilter, ItemFilterConfig> {
	@Override
	public ItemFilterConfig doConvert(ItemFilter source) {
		return new ItemFilterConfig(
				source.isHeroics(),
				source.isRaids(),
				source.isWorldBosses(),
				source.isPvpItems(),
				source.isGreens(),
				source.isLegendaries()
		);
	}

	@Override
	public ItemFilter doConvertBack(ItemFilterConfig source) {
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
