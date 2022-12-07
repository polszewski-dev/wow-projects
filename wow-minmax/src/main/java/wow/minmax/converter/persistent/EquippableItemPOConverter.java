package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.equipment.EquippableItem;
import wow.minmax.converter.ParametrizedConverter;
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
public class EquippableItemPOConverter extends ParametrizedConverter<EquippableItem, EquippableItemPO> {
	private final ItemPOConverter itemPOConverter;
	private final EnchantPOConverter enchantPOConverter;
	private final GemPOConverter gemPOConverter;

	@Override
	protected EquippableItemPO doConvert(EquippableItem item, Map<String, Object> params) {
		return new EquippableItemPO(
				itemPOConverter.convert(item.getItem(), params),
				enchantPOConverter.convert(item.getEnchant(), params),
				item.getSocketCount(),
				gemPOConverter.convertList(item.getGems(), params)
		);
	}

	@Override
	protected EquippableItem doConvertBack(EquippableItemPO value, Map<String, Object> params) {
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
