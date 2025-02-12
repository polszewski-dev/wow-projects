package wow.minmax.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
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
import wow.minmax.model.PlayerCharacter;
import wow.minmax.service.ItemService;

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
	private final Map<String, List<Enchant>> getEnchantsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Enchant>> getBestEnchantsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> getGemsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> getGemsByUniquenessCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem>> getBestNonUniqueGemsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem[]>> getBestGemCombosCache = Collections.synchronizedMap(new HashMap<>());

	public CachedItemService(@Qualifier("itemServiceImpl") ItemService itemService) {
		this.itemService = itemService;
	}

	@Override
	public List<Item> getItemsBySlot(PlayerCharacter character, ItemSlot itemSlot, ItemFilter itemFilter) {
		return getUnfilteredItems(character, itemSlot).stream()
				.filter(itemFilter::matchesFilter)
				.toList();
	}

	private List<Item> getUnfilteredItems(PlayerCharacter character, ItemSlot itemSlot) {
		String key = getProfileKey(character) + "#" + itemSlot;
		return getItemsBySlotCache.computeIfAbsent(key, x -> itemService.getItemsBySlot(character, itemSlot, ItemFilter.everything()));
	}

	@Override
	public List<Enchant> getEnchants(PlayerCharacter character, ItemType itemType, ItemSubType itemSubType) {
		String key = getProfileKey(character) + "#" + itemType + "#" + itemSubType;
		return getEnchantsCache.computeIfAbsent(key, x -> itemService.getEnchants(character, itemType, itemSubType));
	}

	@Override
	public List<Enchant> getBestEnchants(PlayerCharacter character, ItemType itemType, ItemSubType itemSubType) {
		String key = getProfileKey(character) + "#" + itemType + "#" + itemSubType;
		return getBestEnchantsCache.computeIfAbsent(key, x -> itemService.getBestEnchants(character, itemType, itemSubType));
	}

	@Override
	public List<Gem> getGems(PlayerCharacter character, SocketType socketType) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(character) + "#" + meta;
		return getGemsCache.computeIfAbsent(key, x -> itemService.getGems(character, socketType));
	}

	@Override
	public List<Gem> getGems(PlayerCharacter character, SocketType socketType, boolean uniqueness) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(character) + "#" + meta + "#" + uniqueness;
		return getGemsByUniquenessCache.computeIfAbsent(key, x -> itemService.getGems(character, socketType, uniqueness));
	}

	@Override
	public List<Gem> getBestNonUniqueGems(PlayerCharacter character, SocketType socketType) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(character) + "#" + meta;
		return getBestNonUniqueGemsCache.computeIfAbsent(key, x -> itemService.getBestNonUniqueGems(character, socketType));
	}

	@Override
	public List<Gem[]> getBestGemCombos(PlayerCharacter character, Item item, ItemSlotGroup slotGroup, GemFilter gemFilter) {
		if (gemFilter.isUnique()) {
			return itemService.getBestGemCombos(character, item, slotGroup, gemFilter);
		} else {
			String key = getProfileKey(character) + "#" + item.getSocketSpecification().socketTypes();
			return getBestGemCombosCache.computeIfAbsent(key, x -> itemService.getBestGemCombos(character, item, slotGroup, gemFilter));
		}
	}

	private static String getProfileKey(PlayerCharacter character) {
		return character.getCharacterClassId() + "#" +
				character.getLevel() + "#" +
				character.getRaceId() + "#" +
				character.getRole() + "#" +
				character.getPhaseId() + "#" +
				getKey(character.getProfessions().getList(), x -> x.getProfessionId() + "#" + x.getSpecializationId()) + "#" +
				getKey(character.getExclusiveFactions().getList(), Enum::toString);
	}

	private static <T> String getKey(List<T> list, Function<T, String> mapper) {
		return list.stream()
				.map(mapper)
				.sorted()
				.collect(Collectors.joining("#"));
	}
}
