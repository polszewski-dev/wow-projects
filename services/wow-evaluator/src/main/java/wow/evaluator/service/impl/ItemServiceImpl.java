package wow.evaluator.service.impl;

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
import wow.evaluator.model.Player;
import wow.evaluator.service.ItemService;
import wow.evaluator.util.FilterOutWorseEnchantChoices;
import wow.evaluator.util.FilterOutWorseGemChoices;
import wow.evaluator.util.GemComboFinder;

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
	public List<Item> getItemsBySlot(Player player, ItemSlot itemSlot, ItemFilter itemFilter) {
		return itemRepository.getItemsBySlot(itemSlot, player.getPhaseId()).stream()
				.filter(item -> player.canEquip(itemSlot, item))
				.filter(itemFilter::matchesFilter)
				.filter(item -> item.isAvailableTo(player))
				.filter(item -> item.isSuitableFor(player))
				.toList();
	}

	private List<Enchant> getEnchants(Player player, ItemType itemType, ItemSubType itemSubType) {
		return enchantRepository.getEnchants(itemType, itemSubType, player.getPhaseId()).stream()
				.filter(enchant -> enchant.isAvailableTo(player))
				.filter(enchant -> enchant.isSuitableFor(player))
				.toList();
	}

	@Override
	public List<Enchant> getBestEnchants(Player player, ItemType itemType, ItemSubType itemSubType) {
		List<Enchant> enchants = getEnchants(player, itemType, itemSubType);
		return new FilterOutWorseEnchantChoices(enchants).getResult();
	}

	@Override
	public List<Gem> getGems(Player player, SocketType socketType, boolean uniqueness) {
		return gemRepository.getGems(socketType, player.getPhaseId()).stream()
				.filter(gem -> gem.isEffectivelyUnique() == uniqueness)
				.filter(gem -> gem.isAvailableTo(player))
				.filter(gem -> gem.isSuitableFor(player))
				.toList();
	}

	@Override
	public List<Gem> getBestNonUniqueGems(Player player, SocketType socketType) {
		List<Gem> gems = getGems(player, socketType, false);
		if (socketType == SocketType.META) {
			return gems;
		}
		return new FilterOutWorseGemChoices(gems).getResult();
	}

	@Override
	public List<Gem[]> getBestGemCombos(Player player, Item item, ItemSlotGroup slotGroup, GemFilter gemFilter) {
		var finder = new GemComboFinder(player, item.getSocketSpecification(), slotGroup, gemFilter.isUnique(), this);
		return finder.getGemCombos();
	}
}
