package wow.minmax.converter.model.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.EquippableItem;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.pve.PhaseId;
import wow.minmax.model.equipment.EquippableItemConfig;
import wow.minmax.model.equipment.GemConfig;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EquippableItemConfigConverter implements Converter<EquippableItem, EquippableItemConfig>, ParametrizedBackConverter<EquippableItem, EquippableItemConfig, PhaseId> {
	private final ItemConfigConverter itemConfigConverter;
	private final EnchantConfigConverter enchantConfigConverter;
	private final GemConfigConverter gemConfigConverter;

	@Override
	public EquippableItemConfig doConvert(EquippableItem source) {
		return new EquippableItemConfig(
				itemConfigConverter.convert(source.getItem()),
				enchantConfigConverter.convert(source.getEnchant()),
				gemConfigConverter.convertList(source.getGems())
		);
	}

	@Override
	public EquippableItem doConvertBack(EquippableItemConfig source, PhaseId phaseId) {
		EquippableItem equippableItem = new EquippableItem(itemConfigConverter.convertBack(source.getItem(), phaseId));

		equippableItem.enchant(enchantConfigConverter.convertBack(source.getEnchant(), phaseId));

		List<GemConfig> gems = source.getGems();

		for (int i = 0; i < gems.size(); i++) {
			GemConfig gem = gems.get(i);
			equippableItem.insertGem(i, gemConfigConverter.convertBack(gem, phaseId));
		}

		return equippableItem;
	}
}
