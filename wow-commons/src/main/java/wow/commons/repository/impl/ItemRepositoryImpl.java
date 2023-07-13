package wow.commons.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.ItemRepository;
import wow.commons.repository.PveRepository;
import wow.commons.repository.impl.parsers.items.ItemBaseExcelParser;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl extends ExcelRepository implements ItemRepository {
	private final PveRepository pveRepository;

	private final Map<Integer, List<Item>> itemById = new TreeMap<>();
	private final Map<String, List<Item>> itemByName = new TreeMap<>();
	private final Map<ItemType, List<Item>> itemByType = new EnumMap<>(ItemType.class);
	private final Map<ItemSlot, List<Item>> itemBySlot = new EnumMap<>(ItemSlot.class);

	private final Map<String, List<ItemSet>> itemSetByName = new TreeMap<>();

	private final Map<Integer, List<Enchant>> enchantById = new TreeMap<>();
	private final Map<String, List<Enchant>> enchantByName = new TreeMap<>();

	private final Map<Integer, List<Gem>> gemById = new TreeMap<>();
	private final Map<String, List<Gem>> gemByName = new TreeMap<>();
	private final Map<SocketType, List<Gem>> gemBySocketType = new EnumMap<>(SocketType.class);

	private final Map<Integer, List<TradedItem>> tradedItemById = new TreeMap<>();

	@Value("${item.base.xls.file.path}")
	private String itemBaseXlsFilePath;

	@Override
	public Optional<Item> getItem(int itemId, PhaseId phaseId) {
		return getUnique(itemById, itemId, phaseId);
	}

	@Override
	public Optional<Item> getItem(String name, PhaseId phaseId) {
		return getUnique(itemByName, name, phaseId);
	}

	@Override
	public List<Item> getItemsBySlot(ItemSlot itemSlot, PhaseId phaseId) {
		return getList(itemBySlot, itemSlot, phaseId);
	}

	@Override
	public Optional<ItemSet> getItemSet(String name, PhaseId phaseId) {
		return getUnique(itemSetByName, name, phaseId);
	}

	@Override
	public Optional<Enchant> getEnchant(int enchantId, PhaseId phaseId) {
		return getUnique(enchantById, enchantId, phaseId);
	}

	@Override
	public Optional<Enchant> getEnchant(String name, PhaseId phaseId) {
		return getUnique(enchantByName, name, phaseId);
	}

	@Override
	public List<Enchant> getEnchants(ItemType itemType, ItemSubType itemSubType, PhaseId phaseId) {
		return enchantByName.values().stream()
				.flatMap(Collection::stream)
				.filter(x -> x.isAvailableDuring(phaseId))
				.filter(x -> x.matches(itemType, itemSubType))
				.toList();
	}

	@Override
	public Optional<Gem> getGem(int gemId, PhaseId phaseId) {
		return getUnique(gemById, gemId, phaseId);
	}

	@Override
	public Optional<Gem> getGem(String name, PhaseId phaseId) {
		return getUnique(gemByName, name, phaseId);
	}

	@Override
	public List<Gem> getGems(SocketType socketType, PhaseId phaseId) {
		return getList(gemBySocketType, socketType, phaseId);
	}

	@Override
	public Optional<TradedItem> getTradedItem(int tradedItemId, PhaseId phaseId) {
		return getUnique(tradedItemById, tradedItemId, phaseId);
	}

	@PostConstruct
	public void init() throws IOException {
		var itemBaseExcelParser = new ItemBaseExcelParser(itemBaseXlsFilePath, this, pveRepository);
		itemBaseExcelParser.readFromXls();
	}

	public void addItem(Item item) {
		addEntry(itemById, item.getId(), item);
		addEntry(itemByName, item.getName(), item);
		addEntry(itemByType, item.getItemType(), item);
		for (ItemSlot itemSlot : item.getItemType().getItemSlots()) {
			addEntry(itemBySlot, itemSlot, item);
		}
	}

	public void addTradedItem(TradedItem tradedItem) {
		addEntry(tradedItemById, tradedItem.getId(), tradedItem);
	}

	public void addGem(Gem gem) {
		addEntry(gemById, gem.getId(), gem);
		addEntry(gemByName, gem.getName(), gem);
		for (SocketType socketType : SocketType.values()) {
			if (socketType.accepts(gem.getColor())) {
				addEntry(gemBySocketType, socketType, gem);
			}
		}
	}

	public void addItemSet(ItemSet itemSet) {
		addEntry(itemSetByName, itemSet.getName(), itemSet);
	}

	public void addEnchant(Enchant enchant) {
		addEntry(enchantById, enchant.getId(), enchant);
		addEntry(enchantByName, enchant.getName(), enchant);
	}
}
