package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.ItemFilter;
import wow.character.service.ItemService;
import wow.character.service.impl.enumerator.FilterOutWorseEnchantChoices;
import wow.character.service.impl.enumerator.FilterOutWorseGemChoices;
import wow.character.service.impl.enumerator.GemComboFinder;
import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.ItemRepository;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;

	@Override
	public Item getItem(int itemId, PhaseId phaseId) {
		return itemRepository.getItem(itemId, phaseId).orElseThrow();
	}

	@Override
	public Enchant getEnchant(int enchantId, PhaseId phaseId) {
		return itemRepository.getEnchant(enchantId, phaseId).orElseThrow();
	}

	@Override
	public Gem getGem(int gemId, PhaseId phaseId) {
		return itemRepository.getGem(gemId, phaseId).orElseThrow();
	}

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
		return itemRepository.getEnchants(itemType, itemSubType, character.getPhaseId()).stream()
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
	public List<Gem> getGems(PlayerCharacter character, SocketType socketType, boolean nonUniqueOnly) {
		return itemRepository.getGems(socketType, character.getPhaseId()).stream()
				.filter(gem -> !nonUniqueOnly || !(gem.isUnique() || gem.isAvailableOnlyByQuests() || gem.getBinding() == Binding.BINDS_ON_PICK_UP))
				.filter(gem -> gem.isAvailableTo(character))
				.filter(gem -> gem.isSuitableFor(character))
				.toList();
	}

	@Override
	public List<Gem> getBestGems(PlayerCharacter character, SocketType socketType) {
		List<Gem> gems = getGems(character, socketType, true);
		if (socketType == SocketType.META) {
			return gems;
		}
		return new FilterOutWorseGemChoices(gems).getResult();
	}

	@Override
	public List<Gem[]> getBestGemCombos(PlayerCharacter character, Item item) {
		var finder = new GemComboFinder(character, item.getSocketSpecification(), this);
		return finder.getGemCombos();
	}
}
