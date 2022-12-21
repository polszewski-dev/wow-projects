package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.config.ItemConfig;
import wow.character.model.character.Character;
import wow.character.service.ItemService;
import wow.character.service.impl.classifiers.PveRoleStatClassifier;
import wow.character.service.impl.enumerators.FilterOutWorseEnchantChoices;
import wow.character.service.impl.enumerators.FilterOutWorseGemChoices;
import wow.character.service.impl.enumerators.GemComboFinder;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.model.pve.Phase;
import wow.commons.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
	private final ItemRepository itemRepository;
	private final ItemConfig itemConfig;

	private final List<PveRoleStatClassifier> pveRoleStatClassifiers;

	private final GemComboFinder gemComboFinder = new GemComboFinder(this);

	@Override
	public Item getItem(int itemId, Phase phase) {
		return itemRepository.getItem(itemId, phase).orElseThrow();
	}

	@Override
	public Enchant getEnchant(int enchantId, Phase phase) {
		return itemRepository.getEnchant(enchantId, phase).orElseThrow();
	}

	@Override
	public Gem getGem(int gemId, Phase phase) {
		return itemRepository.getGem(gemId, phase).orElseThrow();
	}

	@Override
	public List<Item> getItemsBySlot(Character character, ItemSlot itemSlot) {
		return itemRepository.getItemsBySlot(itemSlot, character.getPhase()).stream()
				.filter(item -> character.canEquip(itemSlot, item))
				.filter(this::meetsConfigFilter)
				.filter(item -> item.isAvailableTo(character))
				.filter(item -> getStatClassifier(character).hasStatsSuitableForRole(item, character))
				.collect(Collectors.toList());
	}

	private boolean meetsConfigFilter(Item item) {
		return item.getItemLevel() >= itemConfig.getMinItemLevel() &&
				item.getRarity().isAtLeastAsGoodAs(itemConfig.getMinRarity()) &&
				!item.isPvPReward() || itemConfig.isIncludePvpItems();
	}

	@Override
	public List<Enchant> getEnchants(Character character, ItemType itemType) {
		return itemRepository.getEnchants(itemType, character.getPhase()).stream()
				.filter(enchant -> enchant.isAvailableTo(character))
				.filter(enchant -> getStatClassifier(character).hasStatsSuitableForRole(enchant, itemType, character))
				.collect(Collectors.toList());
	}

	@Override
	public List<Enchant> getBestEnchants(Character character, ItemType itemType) {
		List<Enchant> enchants = getEnchants(character, itemType);
		return new FilterOutWorseEnchantChoices(enchants).getResult();
	}

	@Override
	public List<Gem> getGems(Character character, SocketType socketType, boolean nonUniqueOnly) {
		return itemRepository.getGems(socketType, character.getPhase()).stream()
				.filter(gem -> !nonUniqueOnly || !(gem.isUnique() || gem.isAvailableOnlyByQuests()))
				.filter(gem -> gem.isAvailableTo(character))
				.filter(gem -> getStatClassifier(character).hasStatsSuitableForRole(gem, character))
				.collect(Collectors.toList());
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
		return gemComboFinder.getGemCombos(character, item.getSocketSpecification());
	}

	private PveRoleStatClassifier getStatClassifier(Character character) {
		return pveRoleStatClassifiers.stream()
				.filter(x -> x.getRole() == character.getRole())
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Unsupported role: " + character.getRole()));
	}
}
