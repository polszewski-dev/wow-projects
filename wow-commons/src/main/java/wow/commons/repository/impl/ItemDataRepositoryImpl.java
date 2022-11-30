package wow.commons.repository.impl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Repository;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.repository.ItemDataRepository;
import wow.commons.repository.PveRepository;
import wow.commons.repository.impl.parsers.items.ItemBaseExcelParser;
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
	private final PveRepository pveRepository;

	private final Map<Integer, Item> itemById = new TreeMap<>();
	private final Map<String, List<Item>> itemByName = new TreeMap<>();
	private final Map<ItemType, List<Item>> itemByType = new TreeMap<>();

	private final Map<String, ItemSet> itemSetByName = new TreeMap<>();

	private final Map<Integer, Enchant> enchantById = new TreeMap<>();
	private final Map<String, Enchant> enchantByName = new TreeMap<>();

	private final Map<Integer, Gem> gemById = new TreeMap<>();
	private final Map<String, List<Gem>> gemByName = new TreeMap<>();

	private final Map<Integer, TradedItem> tradedItemById = new TreeMap<>();

	public ItemDataRepositoryImpl(PveRepository pveRepository) {
		this.pveRepository = pveRepository;
	}

	@Override
	public Optional<Item> getItem(int itemId) {
		return Optional.ofNullable(itemById.get(itemId));
	}

	@Override
	public Optional<Item> getItem(String name) {
		return getUniqueByName(name, itemByName);
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
	public List<Item> getItemsByType(ItemType itemType) {
		return Collections.unmodifiableList(itemByType.getOrDefault(itemType, List.of()));
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
	public List<Enchant> getEnchants(ItemType itemType) {
		return enchantById.values()
				.stream()
				.filter(enchant -> enchant.matches(itemType))
				.collect(Collectors.toList());
	}

	@Override
	public Optional<Gem> getGem(int gemId) {
		return Optional.ofNullable(gemById.get(gemId));
	}

	@Override
	public Optional<Gem> getGem(String name) {
		return getUniqueByName(name, gemByName);
	}

	@Override
	public List<Gem> getAllGems() {
		return new ArrayList<>(gemById.values());
	}

	@Override
	public Optional<TradedItem> getTradedItem(int tradedItemId) {
		return Optional.ofNullable(tradedItemById.get(tradedItemId));
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var itemExcelParser = new ItemExcelParser(this);
		itemExcelParser.readFromXls();

		ItemBaseExcelParser itemBaseExcelParser = new ItemBaseExcelParser(this, pveRepository);
		itemBaseExcelParser.readFromXls();
	}

	public void addItem(Item item) {
		itemById.put(item.getId(), item);
		itemByName.computeIfAbsent(item.getName(), x -> new ArrayList<>()).add(item);
		itemByType.computeIfAbsent(item.getItemType(), x -> new ArrayList<>()).add(item);
	}

	public void addTradedItem(TradedItem tradedItem) {
		tradedItemById.put(tradedItem.getId(), tradedItem);
	}

	public void addGem(Gem gem) {
		gemById.put(gem.getId(), gem);
		gemByName.computeIfAbsent(gem.getName(), x -> new ArrayList<>()).add(gem);
	}

	public void addItemSet(ItemSet itemSet) {
		itemSetByName.put(itemSet.getName(), itemSet);
	}

	public void addEnchant(Enchant enchant) {
		enchantById.put(enchant.getId(), enchant);
		enchantByName.put(enchant.getName(), enchant);
	}

	private <T> Optional<T> getUniqueByName(String name, Map<String, List<T>> byName) {
		List<T> result = byName.get(name);

		if (result == null || result.isEmpty()) {
			return Optional.empty();
		}
		if (result.size() == 1) {
			return Optional.of(result.get(0));
		}
		throw new IllegalArgumentException("Multiple results for: " + name);
	}
}
