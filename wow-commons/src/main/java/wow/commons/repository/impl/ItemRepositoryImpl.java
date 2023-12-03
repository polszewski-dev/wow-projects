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
import wow.commons.repository.SpellRepository;
import wow.commons.repository.impl.parser.item.ItemBaseExcelParser;
import wow.commons.util.CollectionUtil;
import wow.commons.util.PhaseMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static wow.commons.util.PhaseMap.addEntryForEveryPhase;
import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
	private final PveRepository pveRepository;
	private final SpellRepository spellRepository;

	private final PhaseMap<Integer, Item> itemById = new PhaseMap<>();
	private final PhaseMap<String, List<Item>> itemByName = new PhaseMap<>();
	private final PhaseMap<ItemSlot, List<Item>> itemBySlot = new PhaseMap<>();

	private final PhaseMap<String, ItemSet> itemSetByName = new PhaseMap<>();

	private final PhaseMap<Integer, Enchant> enchantById = new PhaseMap<>();
	private final PhaseMap<String, List<Enchant>> enchantByName = new PhaseMap<>();

	private final PhaseMap<Integer, Gem> gemById = new PhaseMap<>();
	private final PhaseMap<String, List<Gem>> gemByName = new PhaseMap<>();
	private final PhaseMap<SocketType, List<Gem>> gemBySocketType = new PhaseMap<>();

	private final PhaseMap<Integer, TradedItem> tradedItemById = new PhaseMap<>();

	@Value("${item.base.xls.file.path}")
	private String itemBaseXlsFilePath;

	@Override
	public Optional<Item> getItem(int itemId, PhaseId phaseId) {
		return itemById.getOptional(phaseId, itemId);
	}

	@Override
	public Optional<Item> getItem(String name, PhaseId phaseId) {
		return itemByName.getOptional(phaseId, name)
				.flatMap(CollectionUtil::getUniqueResult);
	}

	@Override
	public List<Item> getItemsBySlot(ItemSlot itemSlot, PhaseId phaseId) {
		return itemBySlot.getOptional(phaseId, itemSlot)
				.orElse(List.of());
	}

	@Override
	public Optional<ItemSet> getItemSet(String name, PhaseId phaseId) {
		return itemSetByName.getOptional(phaseId, name);
	}

	@Override
	public Optional<Enchant> getEnchant(int enchantId, PhaseId phaseId) {
		return enchantById.getOptional(phaseId, enchantId);
	}

	@Override
	public Optional<Enchant> getEnchant(String name, PhaseId phaseId) {
		return enchantByName.getOptional(phaseId, name)
				.flatMap(CollectionUtil::getUniqueResult);
	}

	@Override
	public List<Enchant> getEnchants(ItemType itemType, ItemSubType itemSubType, PhaseId phaseId) {
		return enchantByName.values(phaseId).stream()
				.flatMap(Collection::stream)
				.filter(x -> x.isAvailableDuring(phaseId))
				.filter(x -> x.matches(itemType, itemSubType))
				.toList();
	}

	@Override
	public Optional<Gem> getGem(int gemId, PhaseId phaseId) {
		return gemById.getOptional(phaseId, gemId);
	}

	@Override
	public Optional<Gem> getGem(String name, PhaseId phaseId) {
		return gemByName.getOptional(phaseId, name)
				.flatMap(CollectionUtil::getUniqueResult);
	}

	@Override
	public List<Gem> getGems(SocketType socketType, PhaseId phaseId) {
		return gemBySocketType.getOptional(phaseId, socketType)
				.orElse(List.of());
	}

	@Override
	public Optional<TradedItem> getTradedItem(int tradedItemId, PhaseId phaseId) {
		return tradedItemById.getOptional(phaseId, tradedItemId);
	}

	@PostConstruct
	public void init() throws IOException {
		var itemBaseExcelParser = new ItemBaseExcelParser(itemBaseXlsFilePath, this, pveRepository, spellRepository);
		itemBaseExcelParser.readFromXls();
	}

	public void addItem(Item item) {
		putForEveryPhase(itemById, item.getId(), item);
		addEntryForEveryPhase(itemByName, item.getName(), item);
		for (var itemSlot : item.getItemType().getItemSlots()) {
			addEntryForEveryPhase(itemBySlot, itemSlot, item);
		}
	}

	public void addTradedItem(TradedItem tradedItem) {
		putForEveryPhase(tradedItemById, tradedItem.getId(), tradedItem);
	}

	public void addGem(Gem gem) {
		putForEveryPhase(gemById, gem.getId(), gem);
		addEntryForEveryPhase(gemByName, gem.getName(), gem);
		for (var socketType : SocketType.values()) {
			if (socketType.accepts(gem.getColor())) {
				addEntryForEveryPhase(gemBySocketType, socketType, gem);
			}
		}
	}

	public void addItemSet(ItemSet itemSet) {
		putForEveryPhase(itemSetByName, itemSet.getName(), itemSet);
	}

	public void addEnchant(Enchant enchant) {
		putForEveryPhase(enchantById, enchant.getId(), enchant);
		addEntryForEveryPhase(enchantByName, enchant.getName(), enchant);
	}
}
