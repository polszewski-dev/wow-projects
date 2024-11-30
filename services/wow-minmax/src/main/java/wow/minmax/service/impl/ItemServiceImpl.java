package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.equipment.GemFilter;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.repository.item.EnchantRepository;
import wow.commons.repository.item.GemRepository;
import wow.commons.repository.item.ItemRepository;
import wow.minmax.model.PlayerCharacter;
import wow.minmax.service.ItemService;
import wow.minmax.service.impl.enumerator.FilterOutWorseEnchantChoices;
import wow.minmax.service.impl.enumerator.FilterOutWorseGemChoices;
import wow.minmax.service.impl.enumerator.GemComboFinder;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;
	private final EnchantRepository enchantRepository;
	private final GemRepository gemRepository;

	@Override
	public List<Item> getItemsBySlot(PlayerCharacter character, ItemSlot itemSlot, ItemFilter itemFilter) {
		return itemRepository.getItemsBySlot(itemSlot, character.getPhaseId()).stream()
				.filter(item -> character.canEquip(itemSlot, item))
				.filter(itemFilter::matchesFilter)
				.filter(item -> item.isAvailableTo(character))
				.filter(item -> item.isSuitableFor(character))
				.toList();
	}

	@Override
	public List<Enchant> getEnchants(PlayerCharacter character, ItemType itemType, ItemSubType itemSubType) {
		return enchantRepository.getEnchants(itemType, itemSubType, character.getPhaseId()).stream()
				.filter(enchant -> enchant.isAvailableTo(character))
				.filter(enchant -> enchant.isSuitableFor(character))
				.toList();
	}

	@Override
	public List<Enchant> getBestEnchants(PlayerCharacter character, ItemType itemType, ItemSubType itemSubType) {
		List<Enchant> enchants = getEnchants(character, itemType, itemSubType);
		return new FilterOutWorseEnchantChoices(enchants).getResult();
	}

	@Override
	public List<Gem> getGems(PlayerCharacter character, SocketType socketType) {
		return gemRepository.getGems(socketType, character.getPhaseId()).stream()
				.filter(gem -> gem.isAvailableTo(character))
				.filter(gem -> gem.isSuitableFor(character))
				.toList();
	}

	@Override
	public List<Gem> getGems(PlayerCharacter character, SocketType socketType, boolean uniqueness) {
		return gemRepository.getGems(socketType, character.getPhaseId()).stream()
				.filter(gem -> gem.isEffectivelyUnique() == uniqueness)
				.filter(gem -> gem.isAvailableTo(character))
				.filter(gem -> gem.isSuitableFor(character))
				.toList();
	}

	@Override
	public List<Gem> getBestNonUniqueGems(PlayerCharacter character, SocketType socketType) {
		List<Gem> gems = getGems(character, socketType, false);
		if (socketType == SocketType.META) {
			return gems;
		}
		return new FilterOutWorseGemChoices(gems).getResult();
	}

	@Override
	public List<Gem[]> getBestGemCombos(PlayerCharacter character, Item item, ItemSlotGroup slotGroup, GemFilter gemFilter) {
		var finder = new GemComboFinder(character, item.getSocketSpecification(), slotGroup, gemFilter.isUnique(), this);
		return finder.getGemCombos();
	}
}
