package wow.evaluator.client.converter.upgrade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.ItemLevelFilter;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.Converter;
import wow.evaluator.client.dto.upgrade.ItemLevelFilterDTO;

/**
 * User: POlszewski
 * Date: 2025-04-11
 */
@Component
@AllArgsConstructor
public class ItemLevelFilterConverter implements Converter<ItemLevelFilter, ItemLevelFilterDTO>, BackConverter<ItemLevelFilter, ItemLevelFilterDTO> {
	@Override
	public ItemLevelFilterDTO doConvert(ItemLevelFilter source) {
		return new ItemLevelFilterDTO(
				source.getMinItemLevelByRarity()
		);
	}

	@Override
	public ItemLevelFilter doConvertBack(ItemLevelFilterDTO source) {
		return new ItemLevelFilter(source.minItemLevelByRarity());
	}
}
