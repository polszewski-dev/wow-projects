package wow.character.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.ItemFilter;
import wow.character.service.ItemService;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.SocketType;
import wow.commons.model.pve.PhaseId;

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
	private final Map<String, List<Gem>> getBestGemsCache = Collections.synchronizedMap(new HashMap<>());
	private final Map<String, List<Gem[]>> getBestGemCombosCache = Collections.synchronizedMap(new HashMap<>());

	public CachedItemService(@Qualifier("itemServiceImpl") ItemService itemService) {
		this.itemService = itemService;
	}

	@Override
	public Item getItem(int itemId, PhaseId phaseId) {
		return itemService.getItem(itemId, phaseId);
	}

	@Override
	public Enchant getEnchant(int enchantId, PhaseId phaseId) {
		return itemService.getEnchant(enchantId, phaseId);
	}

	@Override
	public Gem getGem(int gemId, PhaseId phaseId) {
		return itemService.getGem(gemId, phaseId);
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
	public List<Gem> getGems(PlayerCharacter character, SocketType socketType, boolean nonUniqueOnly) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(character) + "#" + meta + "#" + nonUniqueOnly;
		return getGemsCache.computeIfAbsent(key, x -> itemService.getGems(character, socketType, nonUniqueOnly));
	}

	@Override
	public List<Gem> getBestGems(PlayerCharacter character, SocketType socketType) {
		boolean meta = socketType == SocketType.META;
		String key = getProfileKey(character) + "#" + meta;
		return getBestGemsCache.computeIfAbsent(key, x -> itemService.getBestGems(character, socketType));
	}

	@Override
	public List<Gem[]> getBestGemCombos(PlayerCharacter character, Item item) {
		String key = getProfileKey(character) + "#" + item.getSocketSpecification().socketTypes();
		return getBestGemCombosCache.computeIfAbsent(key, x -> itemService.getBestGemCombos(character, item));
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
