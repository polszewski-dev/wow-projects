package wow.minmax.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
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
import wow.commons.model.pve.Faction;
import wow.minmax.service.ItemService;

import java.util.*;
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
	private final Map<String, List<Enchant>> getEnchantsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> getGemsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> getGemsByUniquenessCache = Collections.synchronizedMap(new HashMap<>());

	public CachedItemService(@Qualifier("itemServiceImpl") ItemService itemService) {
		this.itemService = itemService;
	}

	@Override
	public List<Item> getItemsBySlot(PlayerCharacter player, ItemSlot itemSlot, ItemFilter itemFilter) {
		return getUnfilteredItems(player, itemSlot).stream()
				.filter(itemFilter::matchesFilter)
				.toList();
	}

	private List<Item> getUnfilteredItems(PlayerCharacter player, ItemSlot itemSlot) {
		String key = getProfileKey(player) + "#" + itemSlot;
		return getItemsBySlotCache.computeIfAbsent(key, x -> itemService.getItemsBySlot(player, itemSlot, ItemFilter.everything()));
	}

	@Override
	public List<Enchant> getEnchants(PlayerCharacter player, ItemType itemType, ItemSubType itemSubType) {
		String key = getProfileKey(player) + "#" + itemType + "#" + itemSubType;
		return getEnchantsCache.computeIfAbsent(key, x -> itemService.getEnchants(player, itemType, itemSubType));
	}

	@Override
	public List<Gem> getGems(PlayerCharacter player, SocketType socketType) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(player) + "#" + meta;
		return getGemsCache.computeIfAbsent(key, x -> itemService.getGems(player, socketType));
	}

	@Override
	public List<Gem> getGems(PlayerCharacter player, SocketType socketType, boolean uniqueness) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(player) + "#" + meta + "#" + uniqueness;
		return getGemsByUniquenessCache.computeIfAbsent(key, x -> itemService.getGems(player, socketType, uniqueness));
	}

	private static String getProfileKey(PlayerCharacter player) {
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
				.filter(Objects::nonNull)
				.map(mapper)
				.sorted()
				.collect(Collectors.joining("#"));
	}
}
