package wow.minmax.converter.model.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.EquippableItem;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.item.*;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.EnchantRepository;
import wow.commons.repository.item.GemRepository;
import wow.commons.repository.item.ItemRepository;
import wow.minmax.model.equipment.EquippableItemConfig;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class EquippableItemConfigConverter implements Converter<EquippableItem, EquippableItemConfig>, ParametrizedBackConverter<EquippableItem, EquippableItemConfig, PhaseId> {
	private final ItemRepository itemRepository;
	private final EnchantRepository enchantRepository;
	private final GemRepository gemRepository;

	@Override
	public EquippableItemConfig doConvert(EquippableItem source) {
		var item = source.getItem();
		var enchant = source.getEnchant();
		var gems = source.getGems();

		return new EquippableItemConfig(
				item.getId().value(),
				enchant != null ? enchant.getId().value() : null,
				gems.stream().map(gem -> gem != null ? gem.getId().value() : null).toList()
		);
	}

	@Override
	public EquippableItem doConvertBack(EquippableItemConfig source, PhaseId phaseId) {
		var itemId = ItemId.of(source.getItemId());
		var item = itemRepository.getItem(itemId, phaseId).orElseThrow();
		var enchant = getEnchant(source.getEnchantId(), phaseId);
		var gems = source.getGemIds().stream()
				.map(gemId -> getGem(gemId, phaseId))
				.toList();

		return new EquippableItem(item)
				.enchant(enchant)
				.gem(gems);
	}

	private Enchant getEnchant(Integer enchantId, PhaseId phaseId) {
		return enchantId != null
				? enchantRepository.getEnchant(EnchantId.of(enchantId), phaseId).orElseThrow()
				: null;
	}

	private Gem getGem(Integer gemId, PhaseId phaseId) {
		return gemId != null
				? gemRepository.getGem(GemId.of(gemId), phaseId).orElseThrow()
				: null;
	}
}
