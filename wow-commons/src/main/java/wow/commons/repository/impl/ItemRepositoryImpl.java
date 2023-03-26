package wow.commons.repository.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.pve.Phase;
import wow.commons.repository.ItemRepository;
import wow.commons.repository.PveRepository;
import wow.commons.repository.impl.parsers.items.ItemBaseExcelParser;
import wow.commons.repository.impl.parsers.items.ItemExcelParser;

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
	private final Map<ItemType, List<Enchant>> enchantByItemType = new EnumMap<>(ItemType.class);

	private final Map<Integer, List<Gem>> gemById = new TreeMap<>();
	private final Map<String, List<Gem>> gemByName = new TreeMap<>();
	private final Map<SocketType, List<Gem>> gemBySocketType = new EnumMap<>(SocketType.class);

	private final Map<Integer, List<TradedItem>> tradedItemById = new TreeMap<>();

	@Value("${item.base.xls.file.path}")
	private String itemBaseXlsFilePath;

	@Value("${item.data.xls.file.path}")
	private String itemDataXlsFilePath;

	@Override
	public Optional<Item> getItem(int itemId, Phase phase) {
		return getUnique(itemById, itemId, phase);
	}

	@Override
	public Optional<Item> getItem(String name, Phase phase) {
		return getUnique(itemByName, name, phase);
	}

	@Override
	public List<Item> getItemsBySlot(ItemSlot itemSlot, Phase phase) {
		return getList(itemBySlot, itemSlot, phase);
	}

	@Override
	public Optional<ItemSet> getItemSet(String name, Phase phase) {
		return getUnique(itemSetByName, name, phase);
	}

	@Override
	public Optional<Enchant> getEnchant(int enchantId, Phase phase) {
		return getUnique(enchantById, enchantId, phase);
	}

	@Override
	public Optional<Enchant> getEnchant(String name, Phase phase) {
		return getUnique(enchantByName, name, phase);
	}

	@Override
	public List<Enchant> getEnchants(ItemType itemType, Phase phase) {
		return getList(enchantByItemType, itemType, phase);
	}

	@Override
	public Optional<Gem> getGem(int gemId, Phase phase) {
		return getUnique(gemById, gemId, phase);
	}

	@Override
	public Optional<Gem> getGem(String name, Phase phase) {
		return getUnique(gemByName, name, phase);
	}

	@Override
	public List<Gem> getGems(SocketType socketType, Phase phase) {
		return getList(gemBySocketType, socketType, phase);
	}

	@Override
	public Optional<TradedItem> getTradedItem(int tradedItemId, Phase phase) {
		return getUnique(tradedItemById, tradedItemId, phase);
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var itemExcelParser = new ItemExcelParser(itemDataXlsFilePath, this);
		itemExcelParser.readFromXls();

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
		for (ItemType itemType : enchant.getItemTypes()) {
			addEntry(enchantByItemType, itemType, enchant);
		}
	}
}
