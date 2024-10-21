package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.EquippableItem;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.EquippableItemPO;
import wow.minmax.model.persistent.GemPO;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EquippableItemPOConverter implements Converter<EquippableItem, EquippableItemPO>, ParametrizedBackConverter<EquippableItem, EquippableItemPO> {
	private final ItemPOConverter itemPOConverter;
	private final EnchantPOConverter enchantPOConverter;
	private final GemPOConverter gemPOConverter;

	@Override
	public EquippableItemPO doConvert(EquippableItem source) {
		return new EquippableItemPO(
				itemPOConverter.convert(source.getItem()),
				enchantPOConverter.convert(source.getEnchant()),
				gemPOConverter.convertList(source.getGems())
		);
	}

	@Override
	public EquippableItem doConvertBack(EquippableItemPO source, Map<String, Object> params) {
		EquippableItem equippableItem = new EquippableItem(itemPOConverter.convertBack(source.getItem(), params));

		equippableItem.enchant(enchantPOConverter.convertBack(source.getEnchant(), params));

		List<GemPO> gems = source.getGems();

		for (int i = 0; i < gems.size(); i++) {
			GemPO gem = gems.get(i);
			equippableItem.insertGem(i, gemPOConverter.convertBack(gem, params));
		}

		return equippableItem;
	}
}
