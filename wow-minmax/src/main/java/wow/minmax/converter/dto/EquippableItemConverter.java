package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.EquippableItem;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.EquippableItemDTO;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EquippableItemConverter extends Converter<EquippableItem, EquippableItemDTO> {
	private final ItemConverter itemConverter;
	private final EnchantConverter enchantConverter;
	private final GemConverter gemConverter;

	@Override
	protected EquippableItemDTO doConvert(EquippableItem item) {
		return new EquippableItemDTO(
				itemConverter.convert(item.getItem()),
				enchantConverter.convert(item.getEnchant()),
				gemConverter.convertList(item.getGems())
		);
	}
}
