package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.equipment.EquippableItem;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.EquippableItemPO;

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
				gemPOConverter.convert(item.getGem(1)),
				gemPOConverter.convert(item.getGem(2)),
				gemPOConverter.convert(item.getGem(3))
		);
	}

	@Override
	protected EquippableItem doConvertBack(EquippableItemPO value) {
		EquippableItem equippableItem = new EquippableItem(itemPOConverter.convertBack(value.getItem()));

		equippableItem.enchant(enchantPOConverter.convertBack(value.getEnchant()));

		equippableItem.insertGem(1, gemPOConverter.convertBack(value.getGem1()));
		equippableItem.insertGem(2, gemPOConverter.convertBack(value.getGem2()));
		equippableItem.insertGem(3, gemPOConverter.convertBack(value.getGem3()));

		return equippableItem;
	}
}
