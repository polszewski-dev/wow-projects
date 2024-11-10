package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.EquippableItem;
import wow.commons.client.dto.EquippableItemDTO;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.pve.PhaseId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EquippableItemConverter implements Converter<EquippableItem, EquippableItemDTO>, ParametrizedBackConverter<EquippableItem, EquippableItemDTO, PhaseId> {
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
	public EquippableItem doConvertBack(EquippableItemDTO source, PhaseId phaseId) {
		Item item = itemConverter.convertBack(source.item(), phaseId);
		Enchant enchant = enchantConverter.convertBack(source.enchant(), phaseId);
		List<Gem> gems = gemConverter.convertBackList(source.gems(), phaseId);

		return new EquippableItem(item)
				.enchant(enchant)
				.gem(!gems.isEmpty() ? gems.toArray(Gem[]::new) : new Gem[item.getSocketCount()]);
	}
}
