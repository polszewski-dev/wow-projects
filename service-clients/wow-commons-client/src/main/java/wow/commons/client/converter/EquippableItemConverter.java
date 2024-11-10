package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.client.dto.EquippableItemDTO;

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
	public EquippableItemDTO doConvert(EquippableItem source) {
		return new EquippableItemDTO(
				itemConverter.convert(source.getItem()),
				enchantConverter.convert(source.getEnchant()),
				gemConverter.convertList(source.getGems())
		);
	}

	@Override
	public EquippableItem doConvertBack(EquippableItemDTO source, Map<String, Object> params) {
		Item item = itemConverter.convertBack(source.getItem(), params);
		Enchant enchant = enchantConverter.convertBack(source.getEnchant(), params);
		List<Gem> gems = gemConverter.convertBackList(source.getGems(), params);

		return new EquippableItem(item)
				.enchant(enchant)
				.gem(!gems.isEmpty() ? gems.toArray(Gem[]::new) : new Gem[item.getSocketCount()]);
	}
}
