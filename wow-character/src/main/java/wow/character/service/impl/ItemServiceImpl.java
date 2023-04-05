package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.Character;
import wow.character.model.equipment.ItemFilter;
import wow.character.service.ItemService;
import wow.character.service.impl.classifiers.PveRoleStatClassifier;
import wow.character.service.impl.enumerators.FilterOutWorseEnchantChoices;
import wow.character.service.impl.enumerators.FilterOutWorseGemChoices;
import wow.character.service.impl.enumerators.GemComboFinder;
import wow.commons.model.categorization.Binding;
import wow.commons.model.categorization.ItemSlot;
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

	private final List<PveRoleStatClassifier> pveRoleStatClassifiers;

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
	public List<Item> getItemsBySlot(Character character, ItemSlot itemSlot, ItemFilter itemFilter) {
		return itemRepository.getItemsBySlot(itemSlot, character.getPhaseId()).stream()
				.filter(item -> character.canEquip(itemSlot, item))
				.filter(itemFilter::matchesFilter)
				.filter(item -> item.isAvailableTo(character))
				.filter(item -> getStatClassifier(character).hasStatsSuitableForRole(item, character))
				.toList();
	}

	@Override
	public List<Enchant> getEnchants(Character character, ItemType itemType) {
		return itemRepository.getEnchants(itemType, character.getPhaseId()).stream()
				.filter(enchant -> enchant.isAvailableTo(character))
				.filter(enchant -> getStatClassifier(character).hasStatsSuitableForRole(enchant, itemType, character))
				.toList();
	}

	@Override
	public List<Enchant> getBestEnchants(Character character, ItemType itemType) {
		List<Enchant> enchants = getEnchants(character, itemType);
		return new FilterOutWorseEnchantChoices(enchants).getResult();
	}

	@Override
	public List<Gem> getGems(Character character, SocketType socketType, boolean nonUniqueOnly) {
		return itemRepository.getGems(socketType, character.getPhaseId()).stream()
				.filter(gem -> !nonUniqueOnly || !(gem.isUnique() || gem.isAvailableOnlyByQuests() || gem.getBinding() == Binding.BINDS_ON_PICK_UP))
				.filter(gem -> gem.isAvailableTo(character))
				.filter(gem -> getStatClassifier(character).hasStatsSuitableForRole(gem, character))
				.toList();
	}

	@Override
	public List<Gem> getBestGems(Character character, SocketType socketType) {
		List<Gem> gems = getGems(character, socketType, true);
		if (socketType == SocketType.META) {
			return gems;
		}
		return new FilterOutWorseGemChoices(gems).getResult();
	}

	@Override
	public List<Gem[]> getBestGemCombos(Character character, Item item) {
		return new GemComboFinder(this).getGemCombos(character, item.getSocketSpecification());
	}

	private PveRoleStatClassifier getStatClassifier(Character character) {
		return pveRoleStatClassifiers.stream()
				.filter(x -> x.getRole() == character.getRole())
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Unsupported role: " + character.getRole()));
	}
}
