package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.EquippableItem;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.pve.PhaseId;
import wow.minmax.model.persistent.EquippableItemPO;
import wow.minmax.model.persistent.GemPO;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EquippableItemPOConverter implements Converter<EquippableItem, EquippableItemPO>, ParametrizedBackConverter<EquippableItem, EquippableItemPO, PhaseId> {
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
	public EquippableItem doConvertBack(EquippableItemPO source, PhaseId phaseId) {
		EquippableItem equippableItem = new EquippableItem(itemPOConverter.convertBack(source.getItem(), phaseId));

		equippableItem.enchant(enchantPOConverter.convertBack(source.getEnchant(), phaseId));

		List<GemPO> gems = source.getGems();

		for (int i = 0; i < gems.size(); i++) {
			GemPO gem = gems.get(i);
			equippableItem.insertGem(i, gemPOConverter.convertBack(gem, phaseId));
		}

		return equippableItem;
	}
}
