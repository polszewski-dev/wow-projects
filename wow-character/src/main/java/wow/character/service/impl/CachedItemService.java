package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.Character;
import wow.character.service.ItemService;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.model.pve.Phase;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-14
 */
@Service
@AllArgsConstructor
public class CachedItemService implements ItemService {
	private final ItemService itemService;

	private final Map<String, List<Item>> getItemsBySlotCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Enchant>> getEnchantsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Enchant>> getBestEnchantsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> getGemsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> getBestGemsCache = Collections.synchronizedMap(new HashMap<>());

	@Override
	public Item getItem(int itemId, Phase phase) {
		return itemService.getItem(itemId, phase);
	}

	@Override
	public Enchant getEnchant(int enchantId, Phase phase) {
		return itemService.getEnchant(enchantId, phase);
	}

	@Override
	public Gem getGem(int gemId, Phase phase) {
		return itemService.getGem(gemId, phase);
	}

	@Override
	public List<Item> getItemsBySlot(Character character, ItemSlot itemSlot) {
		String key = getProfileKey(character) + "#" + itemSlot;
		return getItemsBySlotCache.computeIfAbsent(key, x -> itemService.getItemsBySlot(character, itemSlot));
	}

	@Override
	public List<Enchant> getEnchants(Character character, ItemType itemType) {
		String key = getProfileKey(character) + "#" + itemType;
		return getEnchantsCache.computeIfAbsent(key, x -> itemService.getEnchants(character, itemType));
	}

	@Override
	public List<Enchant> getBestEnchants(Character character, ItemType itemType) {
		String key = getProfileKey(character) + "#" + itemType;
		return getBestEnchantsCache.computeIfAbsent(key, x -> itemService.getBestEnchants(character, itemType));
	}

	@Override
	public List<Gem> getGems(Character character, SocketType socketType, boolean nonUniqueOnly) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(character) + "#" + meta + "#" + nonUniqueOnly;
		return getGemsCache.computeIfAbsent(key, x -> itemService.getGems(character, socketType, nonUniqueOnly));
	}

	@Override
	public List<Gem> getBestGems(Character character, SocketType socketType) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(character) + "#" + meta;
		return getBestGemsCache.computeIfAbsent(key, x -> itemService.getBestGems(character, socketType));
	}

	@Override
	public List<Gem[]> getBestGemCombos(Character character, Item item) {
		return itemService.getBestGemCombos(character, item);
	}

	private static String getProfileKey(Character character) {
		return character.getCharacterClass() + "#" +
				character.getRole() + "#" +
				character.getPhase() + "#" +
				character.getProfessions().getList().stream()
						.map(x -> x.getProfession() + "#" + x.getSpecialization())
						.collect(Collectors.joining("#"));
	}
}
