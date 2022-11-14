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

	private final Map<String, Map<ItemSlot, List<Item>>> getItemsBySlotCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, Map<ItemType, List<Item>>> getItemsByTypeCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Enchant>> getEnchantsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> getGemsCache = Collections.synchronizedMap(new HashMap<>());

	@Override
	public Item getItem(int itemId) {
		return itemService.getItem(itemId);
	}

	@Override
	public List<Item> getItems() {
		return itemService.getItems();
	}

	@Override
	public List<Item> getItems(Phase phase) {
		return itemService.getItems(phase);
	}

	@Override
	public List<Item> getItems(Phase phase, ItemSlot slot) {
		return itemService.getItems(phase, slot);
	}

	@Override
	public Map<ItemSlot, List<Item>> getItemsBySlot(PlayerProfile playerProfile) {
		String key = getProfileKey(playerProfile);
		return getItemsBySlotCache.computeIfAbsent(key, x -> itemService.getItemsBySlot(playerProfile));
	}

	@Override
	public Map<ItemType, List<Item>> getItemsByType(PlayerProfile playerProfile) {
		String key = getProfileKey(playerProfile);
		return getItemsByTypeCache.computeIfAbsent(key, x -> itemService.getItemsByType(playerProfile));
	}

	@Override
	public List<Enchant> getEnchants(PlayerProfile playerProfile, ItemType itemType) {
		String key = getProfileKey(playerProfile) + "#" + itemType;
		return getEnchantsCache.computeIfAbsent(key, x -> itemService.getEnchants(playerProfile, itemType));
	}

	@Override
	public List<Gem> getGems(PlayerProfile playerProfile, SocketType socketType, boolean onlyCrafted) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(playerProfile) + "#" + meta + "#" + onlyCrafted;
		return getGemsCache.computeIfAbsent(key, x -> itemService.getGems(playerProfile, socketType, onlyCrafted));
	}

	@Override
	public List<Gem[]> getGemCombos(PlayerProfile playerProfile, Item item) {
		return itemService.getGemCombos(playerProfile, item);
	}

	private static String getProfileKey(PlayerProfile playerProfile) {
		return playerProfile.getCharacterClass() + "#" + playerProfile.getRole() + "#" + playerProfile.getPhase();
	}
}
