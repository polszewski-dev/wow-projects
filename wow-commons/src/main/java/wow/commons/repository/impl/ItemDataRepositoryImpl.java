package wow.commons.repository.impl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Repository;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.CharacterClass;
import wow.commons.repository.ItemDataRepository;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.parsers.items.ItemDatabaseLuaParser;
import wow.commons.repository.impl.parsers.items.ItemExcelParser;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@Repository
public class ItemDataRepositoryImpl implements ItemDataRepository {
	private final PVERepository pveRepository;

	private final Map<Integer, Item> itemById = new TreeMap<>();
	private final Map<String, List<Item>> itemByName = new TreeMap<>();
	private final Map<ItemSet, List<Item>> itemsByItemSet = new HashMap<>();
	private final Map<Item, List<Item>> tokenToItems = new HashMap<>();
	private final Map<Item, List<Item>> itemToTokens = new HashMap<>();

	private final Map<String, ItemSet> itemSetByName = new TreeMap<>();
	private final Map<Integer, Enchant> enchantById = new TreeMap<>();
	private final Map<String, Enchant> enchantByName = new TreeMap<>();
	private final Map<Integer, Gem> gemById = new TreeMap<>();
	private final Map<String, Gem> gemByName = new TreeMap<>();

	private final Map<String, Map<ItemType, List<Item>>> casterItemsByTypeCache = Collections.synchronizedMap(new HashMap<>());

	public ItemDataRepositoryImpl(PVERepository pveRepository) {
		this.pveRepository = pveRepository;
	}

	@Override
	public Optional<Item> getItem(int itemId) {
		return Optional.ofNullable(itemById.get(itemId));
	}

	@Override
	public Optional<Item> getItem(String itemName) {
		List<Item> result = itemByName.get(itemName);

		if (result == null || result.isEmpty()) {
			return Optional.empty();
		}
		if (result.size() == 1) {
			return Optional.of(result.get(0));
		}
		throw new IllegalArgumentException("Multiple results for: " + itemName);
	}

	@Override
	public Optional<Item> getItem(ItemLink itemLink) {
		return getItem(itemLink.getItemId());
	}

	@Override
	public Collection<Item> getAllItems() {
		return Collections.unmodifiableCollection(itemById.values());
	}

	@Override
	public List<Item> getCasterItems(Phase phase, CharacterClass characterClass, SpellSchool spellSchool) {
		return getAllItems().stream()
							.filter(item -> item.isAvailableDuring(phase) && item.isCasterItem(characterClass, spellSchool))
							.collect(Collectors.toList());
	}

	@Override
	public Map<ItemType, List<Item>> getCasterItemsByType(Phase phase, CharacterClass characterClass, SpellSchool spellSchool) {
		return casterItemsByTypeCache.computeIfAbsent(phase + "#" + characterClass + "#" + spellSchool,
				x -> getCasterItems(phase, characterClass, spellSchool)
						.stream()
						.collect(Collectors.groupingBy(Item::getItemType)));
	}

	@Override
	public List<Item> getSetItems(ItemSet itemSet) {
		return itemsByItemSet.get(itemSet);
	}

	@Override
	public List<Item> getItemsTradedFor(ItemLink itemLink) {
		return tokenToItems.getOrDefault(getItem(itemLink).orElseThrow(), List.of());
	}

	@Override
	public List<Item> getSourceItemsFor(ItemLink itemLink) {
		return itemToTokens.getOrDefault(getItem(itemLink).orElseThrow(), List.of());
	}

	@Override
	public List<Item> getEquippableItemsFromRaidDrop(Item item, CharacterClass clazz) {
		List<Item> items = getItemsTradedFor(item.getItemLink());
		if (items.isEmpty()) {
			return List.of(item);
		}
		items = items.stream()
				.filter(item2 -> clazz == null || item2.canBeEquippedBy(clazz))
				.collect(Collectors.toList());
		return items;
	}

	@Override
	public Collection<ItemSet> getAllItemSets() {
		return Collections.unmodifiableCollection(itemSetByName.values());
	}

	@Override
	public Optional<ItemSet> getItemSet(String name) {
		return Optional.ofNullable(itemSetByName.get(name));
	}

	@Override
	public Optional<Enchant> getEnchant(int enchantId) {
		return Optional.ofNullable(enchantById.get(enchantId));
	}

	@Override
	public Optional<Enchant> getEnchant(String name) {
		return Optional.ofNullable(enchantByName.get(name));
	}

	@Override
	public Optional<Gem> getGem(int gemId) {
		return Optional.ofNullable(gemById.get(gemId));
	}

	@Override
	public Optional<Gem> getGem(String name) {
		return Optional.ofNullable(gemByName.get(name));
	}

	@Override
	public List<Gem> getAllGems() {
		return new ArrayList<>(gemByName.values());
	}

	@Override
	public List<Enchant> getEnchants(ItemType itemType) {
		return enchantById.values()
						  .stream()
						  .filter(enchant -> enchant.matches(itemType))
						  .collect(Collectors.toList());
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var itemExcelParser = new ItemExcelParser(this);
		itemExcelParser.readFromXls();

		var itemDabaseLuaParser = new ItemDatabaseLuaParser(this, pveRepository);
		itemDabaseLuaParser.readFromLua();
	}

	public void addItem(Item item) {
		itemById.put(item.getId(), item);
		itemByName.computeIfAbsent(item.getName(), x -> new ArrayList<>()).add(item);
	}

	public void addToken(Item item, Item sourceToken) {
		tokenToItems.computeIfAbsent(sourceToken, x -> new ArrayList<>()).add(item);
		itemToTokens.computeIfAbsent(item, x -> new ArrayList<>()).add(sourceToken);
	}


	public void addSetItem(Item item, ItemSet itemSet) {
		itemsByItemSet.computeIfAbsent(itemSet, x -> new ArrayList<>()).add(item);
	}

	public void addGem(Gem gem) {
		gemById.put(gem.getId(), gem);
		gemByName.put(gem.getName(), gem);
	}

	public void addItemSet(ItemSet itemSet) {
		itemSetByName.put(itemSet.getName(), itemSet);
	}

	public void addEnchant(Enchant enchant) {
		enchantById.put(enchant.getId(), enchant);
		enchantByName.put(enchant.getName(), enchant);
	}
}
