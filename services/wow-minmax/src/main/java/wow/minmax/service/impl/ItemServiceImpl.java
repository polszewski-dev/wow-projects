package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.ItemFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.repository.item.EnchantRepository;
import wow.commons.repository.item.GemRepository;
import wow.commons.repository.item.ItemRepository;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.service.ItemService;

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
	private final MinmaxConfigRepository minmaxConfigRepository;

	@Override
	public List<Item> getItemsBySlot(PlayerCharacter player, ItemSlot itemSlot, ItemFilter itemFilter) {
		var itemLevelFilter = minmaxConfigRepository.getItemLevelFilter(player).orElseThrow();

		return itemRepository.getItemsBySlot(itemSlot, player.getPhaseId()).stream()
				.filter(item -> player.canEquip(itemSlot, item))
				.filter(itemFilter::matchesFilter)
				.filter(item -> item.isAvailableTo(player))
				.filter(item -> item.isSuitableFor(player))
				.filter(itemLevelFilter::matchesFilter)
				.toList();
	}

	@Override
	public List<Enchant> getEnchants(PlayerCharacter player, ItemType itemType, ItemSubType itemSubType) {
		return enchantRepository.getEnchants(itemType, itemSubType, player.getPhaseId()).stream()
				.filter(enchant -> enchant.isAvailableTo(player))
				.filter(enchant -> enchant.isSuitableFor(player))
				.toList();
	}

	@Override
	public List<Gem> getGems(PlayerCharacter player, SocketType socketType) {
		return gemRepository.getGems(socketType, player.getPhaseId()).stream()
				.filter(gem -> gem.isAvailableTo(player))
				.filter(gem -> gem.isSuitableFor(player))
				.toList();
	}

	@Override
	public List<Gem> getGems(PlayerCharacter player, SocketType socketType, boolean uniqueness) {
		return gemRepository.getGems(socketType, player.getPhaseId()).stream()
				.filter(gem -> gem.isEffectivelyUnique() == uniqueness)
				.filter(gem -> gem.isAvailableTo(player))
				.filter(gem -> gem.isSuitableFor(player))
				.toList();
	}
}
