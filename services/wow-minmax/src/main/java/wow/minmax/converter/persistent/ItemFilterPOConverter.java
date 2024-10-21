package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.ItemFilter;
import wow.minmax.converter.BackConverter;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.ItemFilterPO;

/**
 * User: POlszewski
 * Date: 2023-04-05
 */
@Component
@AllArgsConstructor
public class ItemFilterPOConverter implements Converter<ItemFilter, ItemFilterPO>, BackConverter<ItemFilter, ItemFilterPO> {
	@Override
	public ItemFilterPO doConvert(ItemFilter source) {
		return new ItemFilterPO(
				source.isHeroics(),
				source.isRaids(),
				source.isWorldBosses(),
				source.isPvpItems(),
				source.isGreens(),
				source.isLegendaries()
		);
	}

	@Override
	public ItemFilter doConvertBack(ItemFilterPO source) {
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
