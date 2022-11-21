package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.equipment.EquippableItem;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.EquippableItemPO;
import wow.minmax.model.persistent.GemPO;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EquippableItemPOConverter extends Converter<EquippableItem, EquippableItemPO> {
	private final ItemPOConverter itemPOConverter;
	private final EnchantPOConverter enchantPOConverter;
	private final GemPOConverter gemPOConverter;

	@Override
	protected EquippableItemPO doConvert(EquippableItem item) {
		return new EquippableItemPO(
				itemPOConverter.convert(item.getItem()),
				enchantPOConverter.convert(item.getEnchant()),
				item.getSocketCount(),
				gemPOConverter.convertList(item.getGems())
		);
	}

	@Override
	protected EquippableItem doConvertBack(EquippableItemPO value) {
		EquippableItem equippableItem = new EquippableItem(itemPOConverter.convertBack(value.getItem()));

		equippableItem.enchant(enchantPOConverter.convertBack(value.getEnchant()));

		List<GemPO> gems = value.getGems();

		for (int i = 0; i < gems.size(); i++) {
			GemPO gem = gems.get(i);
			equippableItem.insertGem(i, gemPOConverter.convertBack(gem));
		}

		return equippableItem;
	}
}
