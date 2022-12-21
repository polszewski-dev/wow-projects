package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.model.pve.Phase;
import wow.commons.repository.ItemRepository;
import wow.minmax.config.ItemConfig;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.ItemService;
import wow.minmax.service.impl.classifiers.PveRoleStatClassifier;
import wow.minmax.service.impl.enumerators.FilterOutWorseEnchantChoices;
import wow.minmax.service.impl.enumerators.FilterOutWorseGemChoices;
import wow.minmax.service.impl.enumerators.GemComboFinder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Service("nonCachedItemService")
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
	public List<Item> getItemsBySlot(PlayerProfile playerProfile, ItemSlot itemSlot) {
		return itemRepository.getItemsBySlot(itemSlot, playerProfile.getPhase()).stream()
				.filter(item -> playerProfile.canEquip(itemSlot, item))
				.filter(this::meetsConfigFilter)
				.filter(item -> item.isAvailableTo(playerProfile.getCharacter()))
				.filter(item -> getStatClassifier(playerProfile).hasStatsSuitableForRole(item, playerProfile))
				.collect(Collectors.toList());
	}

	private boolean meetsConfigFilter(Item item) {
		return item.getItemLevel() >= itemConfig.getMinItemLevel() &&
				item.getRarity().isAtLeastAsGoodAs(itemConfig.getMinRarity()) &&
				!item.isPvPReward() || itemConfig.isIncludePvpItems();
	}

	@Override
	public List<Enchant> getEnchants(PlayerProfile playerProfile, ItemType itemType) {
		return itemRepository.getEnchants(itemType, playerProfile.getPhase()).stream()
				.filter(enchant -> enchant.isAvailableTo(playerProfile.getCharacter()))
				.filter(enchant -> getStatClassifier(playerProfile).hasStatsSuitableForRole(enchant, itemType, playerProfile))
				.collect(Collectors.toList());
	}

	@Override
	public List<Enchant> getBestEnchants(PlayerProfile playerProfile, ItemType itemType) {
		List<Enchant> enchants = getEnchants(playerProfile, itemType);
		return new FilterOutWorseEnchantChoices(enchants).getResult();
	}

	@Override
	public List<Gem> getGems(PlayerProfile playerProfile, SocketType socketType, boolean nonUniqueOnly) {
		return itemRepository.getGems(socketType, playerProfile.getPhase()).stream()
				.filter(gem -> !nonUniqueOnly || !(gem.isUnique() || gem.isAvailableOnlyByQuests()))
				.filter(gem -> gem.isAvailableTo(playerProfile.getCharacter()))
				.filter(gem -> getStatClassifier(playerProfile).hasStatsSuitableForRole(gem, playerProfile))
				.collect(Collectors.toList());
	}

	@Override
	public List<Gem> getBestGems(PlayerProfile playerProfile, SocketType socketType) {
		List<Gem> gems = getGems(playerProfile, socketType, true);
		if (socketType == SocketType.META) {
			return gems;
		}
		return new FilterOutWorseGemChoices(gems).getResult();
	}

	@Override
	public List<Gem[]> getBestGemCombos(PlayerProfile playerProfile, Item item) {
		return gemComboFinder.getGemCombos(playerProfile, item.getSocketSpecification());
	}

	private PveRoleStatClassifier getStatClassifier(PlayerProfile playerProfile) {
		return pveRoleStatClassifiers.stream()
				.filter(x -> x.getRole() == playerProfile.getRole())
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Unsupported role: " + playerProfile.getRole()));
	}
}
