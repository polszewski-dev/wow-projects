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
	public EquippableItemPO doConvert(EquippableItem item) {
		return new EquippableItemPO(
				itemPOConverter.convert(item.getItem()),
				enchantPOConverter.convert(item.getEnchant()),
				item.getSocketCount(),
				gemPOConverter.convertList(item.getGems())
		);
	}

	@Override
	public EquippableItem doConvertBack(EquippableItemPO value, Map<String, Object> params) {
		EquippableItem equippableItem = new EquippableItem(itemPOConverter.convertBack(value.getItem(), params));

		equippableItem.enchant(enchantPOConverter.convertBack(value.getEnchant(), params));

		List<GemPO> gems = value.getGems();

		for (int i = 0; i < gems.size(); i++) {
			GemPO gem = gems.get(i);
			equippableItem.insertGem(i, gemPOConverter.convertBack(gem, params));
		}

		return equippableItem;
	}
}
