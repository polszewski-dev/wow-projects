package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.dto.EquippableItemDTO;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EquippableItemConverter implements Converter<EquippableItem, EquippableItemDTO>, ParametrizedBackConverter<EquippableItem, EquippableItemDTO> {
	private final ItemConverter itemConverter;
	private final EnchantConverter enchantConverter;
	private final GemConverter gemConverter;

	@Override
	public EquippableItemDTO doConvert(EquippableItem item) {
		return new EquippableItemDTO(
				itemConverter.convert(item.getItem()),
				enchantConverter.convert(item.getEnchant()),
				gemConverter.convertList(item.getGems())
		);
	}

	@Override
	public EquippableItem doConvertBack(EquippableItemDTO value, Map<String, Object> params) {
		Item item = itemConverter.convertBack(value.getItem(), params);
		Enchant enchant = enchantConverter.convertBack(value.getEnchant(), params);
		List<Gem> gems = gemConverter.convertBackList(value.getGems(), params);

		return new EquippableItem(item)
				.enchant(enchant)
				.gem(gems.toArray(Gem[]::new));
	}
}
