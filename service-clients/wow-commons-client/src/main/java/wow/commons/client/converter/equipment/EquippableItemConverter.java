package wow.commons.client.converter.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.EquippableItem;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.client.dto.equipment.EquippableItemDTO;
import wow.commons.model.pve.PhaseId;

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
		var item = itemConverter.convertBack(source.item(), phaseId);
		var enchant = enchantConverter.convertBack(source.enchant(), phaseId);
		var gems = gemConverter.convertBackList(source.gems(), phaseId);

		return new EquippableItem(item)
				.enchant(enchant)
				.gem(gems);
	}
}
