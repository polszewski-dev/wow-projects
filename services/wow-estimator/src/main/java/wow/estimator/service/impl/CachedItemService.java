package wow.estimator.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import wow.character.model.equipment.GemFilter;
import wow.character.model.equipment.ItemFilter;
import wow.character.model.equipment.ItemLevelFilter;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSlotGroup;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.model.pve.Faction;
import wow.estimator.model.Player;
import wow.estimator.service.ItemService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-14
 */
@Primary
@Service
public class CachedItemService implements ItemService {
	private final ItemService itemService;

	private final Map<String, List<Item>> getItemsBySlotCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Enchant>> getBestEnchantsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> getGemsByUniquenessCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> getBestNonUniqueGemsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem[]>> getBestGemCombosCache = Collections.synchronizedMap(new HashMap<>());

	public CachedItemService(@Qualifier("itemServiceImpl") ItemService itemService) {
		this.itemService = itemService;
	}

	@Override
	public List<Item> getItemsBySlot(Player player, ItemSlot itemSlot, ItemFilter itemFilter, ItemLevelFilter itemLevelFilter) {
		return getUnfilteredItems(player, itemSlot).stream()
				.filter(itemFilter::matchesFilter)
				.filter(itemLevelFilter::matchesFilter)
				.toList();
	}

	private List<Item> getUnfilteredItems(Player player, ItemSlot itemSlot) {
		String key = getProfileKey(player) + "#" + itemSlot;
		return getItemsBySlotCache.computeIfAbsent(
				key,
				x -> itemService.getItemsBySlot(player, itemSlot, ItemFilter.everything(), ItemLevelFilter.everything())
		);
	}

	@Override
	public List<Enchant> getBestEnchants(Player player, ItemType itemType, ItemSubType itemSubType) {
		String key = getProfileKey(player) + "#" + itemType + "#" + itemSubType;
		return getBestEnchantsCache.computeIfAbsent(key, x -> itemService.getBestEnchants(player, itemType, itemSubType));
	}

	@Override
	public List<Gem> getGems(Player player, SocketType socketType, boolean uniqueness) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(player) + "#" + meta + "#" + uniqueness;
		return getGemsByUniquenessCache.computeIfAbsent(key, x -> itemService.getGems(player, socketType, uniqueness));
	}

	@Override
	public List<Gem> getBestNonUniqueGems(Player player, SocketType socketType) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(player) + "#" + meta;
		return getBestNonUniqueGemsCache.computeIfAbsent(key, x -> itemService.getBestNonUniqueGems(player, socketType));
	}

	@Override
	public List<Gem[]> getBestGemCombos(Player player, Item item, ItemSlotGroup slotGroup, GemFilter gemFilter) {
		if (gemFilter.isUnique()) {
			return itemService.getBestGemCombos(player, item, slotGroup, gemFilter);
		} else {
			String key = getProfileKey(player) + "#" + item.getSocketSpecification().socketTypes();
			return getBestGemCombosCache.computeIfAbsent(key, x -> itemService.getBestGemCombos(player, item, slotGroup, gemFilter));
		}
	}

	private static String getProfileKey(Player player) {
		return player.getCharacterClassId() + "#" +
				player.getLevel() + "#" +
				player.getRaceId() + "#" +
				player.getRole() + "#" +
				player.getPhaseId() + "#" +
				getKey(player.getProfessions().getList(), x -> x.professionId() + "#" + x.specializationId()) + "#" +
				getKey(player.getExclusiveFactions().getList(), Faction::getName);
	}

	private static <T> String getKey(List<T> list, Function<T, String> mapper) {
		return list.stream()
				.map(mapper)
				.sorted()
				.collect(Collectors.joining("#"));
	}
}
