package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.minmax.model.PlayerProfile;
import wow.minmax.service.ItemService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-14
 */
@Service("cachedItemService")
@AllArgsConstructor
public class CachedItemService implements ItemService {
	private final ItemService itemService;

	private final Map<String, List<Item>> getItemsBySlotCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Item>> getItemsByTypeCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Enchant>> getEnchantsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Enchant>> getBestEnchantsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> getGemsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> getBestGemsCache = Collections.synchronizedMap(new HashMap<>());

	@Override
	public Item getItem(int itemId) {
		return itemService.getItem(itemId);
	}

	@Override
	public List<Item> getItemsBySlot(PlayerProfile playerProfile, ItemSlot itemSlot) {
		String key = getProfileKey(playerProfile) + "#" + itemSlot;
		return getItemsBySlotCache.computeIfAbsent(key, x -> itemService.getItemsBySlot(playerProfile, itemSlot));
	}

	@Override
	public List<Item> getItemsByType(PlayerProfile playerProfile, ItemType itemType) {
		String key = getProfileKey(playerProfile) + "#" + itemType;
		return getItemsByTypeCache.computeIfAbsent(key, x -> itemService.getItemsByType(playerProfile, itemType));
	}

	@Override
	public List<Enchant> getEnchants(PlayerProfile playerProfile, ItemType itemType) {
		String key = getProfileKey(playerProfile) + "#" + itemType;
		return getEnchantsCache.computeIfAbsent(key, x -> itemService.getEnchants(playerProfile, itemType));
	}

	@Override
	public List<Enchant> getBestEnchants(PlayerProfile playerProfile, ItemType itemType) {
		String key = getProfileKey(playerProfile) + "#" + itemType;
		return getBestEnchantsCache.computeIfAbsent(key, x -> itemService.getBestEnchants(playerProfile, itemType));
	}

	@Override
	public List<Gem> getGems(PlayerProfile playerProfile, SocketType socketType, boolean nonUniqueOnly) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(playerProfile) + "#" + meta + "#" + nonUniqueOnly;
		return getGemsCache.computeIfAbsent(key, x -> itemService.getGems(playerProfile, socketType, nonUniqueOnly));
	}

	@Override
	public List<Gem> getBestGems(PlayerProfile playerProfile, SocketType socketType) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(playerProfile) + "#" + meta;
		return getBestGemsCache.computeIfAbsent(key, x -> itemService.getBestGems(playerProfile, socketType));
	}

	@Override
	public List<Gem[]> getBestGemCombos(PlayerProfile playerProfile, Item item) {
		return itemService.getBestGemCombos(playerProfile, item);
	}

	private static String getProfileKey(PlayerProfile playerProfile) {
		return playerProfile.getCharacterClass() + "#" + playerProfile.getRole() + "#" + playerProfile.getPhase();
	}
}
