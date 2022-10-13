package wow.commons.repository.impl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.springframework.stereotype.Repository;
import wow.commons.model.categorization.ItemCategory;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.pve.Raid;
import wow.commons.model.sources.Source;
import wow.commons.model.sources.SourceParser;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.CharacterClass;
import wow.commons.repository.ItemDataRepository;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.parsers.GemLuaParser;
import wow.commons.repository.impl.parsers.ItemExcelParser;
import wow.commons.repository.impl.parsers.ItemLuaParser;
import wow.commons.repository.impl.parsers.ItemStatParser;

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
	private final Map<ItemLink, Item> itemByLink = new TreeMap<>();
	private final Map<String, List<Item>> itemByName = new TreeMap<>();
	private final Map<ItemSet, List<Item>> tierSets = new HashMap<>();
	private final Map<Item, List<Item>> tokenToItems = new HashMap<>();
	private final Map<Item, List<Item>> itemToTokens = new HashMap<>();

	private final Map<String, ItemSet> itemSetByName = new TreeMap<>();
	private final Map<Integer, Enchant> enchantById = new TreeMap<>();
	private final Map<String, Enchant> enchantByName = new TreeMap<>();
	private final Map<Integer, Gem> gemById = new TreeMap<>();
	private final Map<String, Gem> gemByName = new TreeMap<>();

	private Map<String, Map<ItemType, List<Item>>> casterItemsByTypeCache = Collections.synchronizedMap(new HashMap<>());

	public ItemDataRepositoryImpl(PVERepository pveRepository) {
		this.pveRepository = pveRepository;
	}

	@Override
	public Item getItem(int itemId) {
		return itemById.get(itemId);
	}

	@Override
	public Item getItem(String itemName) {
		List<Item> result = itemByName.get(itemName);

		if (result == null || result.isEmpty()) {
			return null;
		}
		if (result.size() == 1) {
			return result.get(0);
		}
		throw new IllegalArgumentException("Multiple results for: " + itemName);
	}

	@Override
	public Item getItem(ItemLink itemLink) {
		return getItem(itemLink.getId());
	}

	@Override
	public Collection<Item> getAllItems() {
		return Collections.unmodifiableCollection(itemById.values());
	}

	@Override
	public List<Item> getCasterItems(int phase, CharacterClass characterClass, SpellSchool spellSchool) {
		return getAllItems().stream()
							.filter(item -> isCasterItem(item, phase, characterClass, spellSchool))
							.collect(Collectors.toList());
	}

	@Override
	public Map<ItemType, List<Item>> getCasterItemsByType(int phase, CharacterClass characterClass, SpellSchool spellSchool) {
		return casterItemsByTypeCache.computeIfAbsent(phase + "#" + characterClass + "#" + spellSchool,
				x -> getCasterItems(phase, characterClass, spellSchool)
						.stream()
						.collect(Collectors.groupingBy(Item::getItemType)));
	}

	@Override
	public int getPhase(Item item) {
		return item.getSources()
				   .stream()
				   .map(source -> source.isTradedFromToken() ? getPhase(getItem(source.getTradedFromToken())) : source.getPhase())
				   .min(Integer::compareTo)
				   .orElse(-1);
	}

	private boolean isCasterItem(Item item, int phase, CharacterClass characterClass, SpellSchool spellSchool) {
		if (getPhase(item) > phase) {
			return false;
		}
		ItemCategory category = item.getItemType().getCategory();
		if (!(category == ItemCategory.Armor || category == ItemCategory.Accessory || category == ItemCategory.Weapon)) {
			return false;
		}
		if (!item.canBeEquippedBy(characterClass)) {
			return false;
		}
		if (item.hasCasterStats(spellSchool)) {
			return true;
		}
		if (item.getClassRestriction().contains(characterClass)) {
			return true;
		}
		if (item.getItemType() == ItemType.Trinket) {
			return item.getSpecialAbilities()
					   .stream()
					   .anyMatch(x -> x.getLine().contains("spell"));
		}
		if (item.getName().equals("Shroud of the Highborne")) {
			return true;
		}
		return false;
	}

	@Override
	public Set<Raid> getRaidSources(Item item) {
		Set<Raid> result = new LinkedHashSet<>();

		for (Source source : item.getSources()) {
			if (source.isRaidDrop()) {
				result.add((Raid)source.getInstance());
			} else if (source.isTradedFromToken()) {
				Item token = getItem(source.getTradedFromToken());
				if (token.isSourcedFromRaid()) {
					result.addAll(getRaidSources(token));
				}
			}
		}

		return result;
	}

	@Override
	public List<Item> getTierItems(ItemSet tierSet) {
		return tierSets.get(tierSet);
	}

	@Override
	public List<Item> getItemsTradedFor(ItemLink itemLink) {
		return tokenToItems.getOrDefault(getItem(itemLink), List.of());
	}

	@Override
	public List<Item> getSourceItemsFor(ItemLink itemLink) {
		return itemToTokens.getOrDefault(getItem(itemLink), List.of());
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
	public ItemSet getItemSet(String name) {
		return itemSetByName.get(name);
	}

	@Override
	public Enchant getEnchant(int enchantId) {
		if (!enchantById.containsKey(enchantId)) {
			throw new IllegalArgumentException("No enchant: " + enchantId);
		}
		return enchantById.get(enchantId);
	}

	@Override
	public Enchant getEnchant(String name) {
		return enchantByName.get(name);
	}

	@Override
	public Gem getGem(int gemId) {
		if (!gemById.containsKey(gemId)) {
			throw new IllegalArgumentException("No gem: " + gemId);
		}
		return gemById.get(gemId);
	}

	@Override
	public Gem getGem(String name) {
		return gemByName.get(name);
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
		readFromLua("lua/item_tooltips_dungeons.lua");
		readFromLua("lua/item_tooltips_factions.lua");
		readFromLua("lua/item_tooltips_crafted.lua");
		readFromLua("lua/item_tooltips_badge.lua");
		readFromLua("lua/item_tooltips_badge_later.lua");
		readFromLua("lua/item_tooltips_t4.lua");
		readFromLua("lua/item_tooltips_t5.lua");
		readFromLua("lua/item_tooltips_t6.lua");
		readFromLua("lua/item_tooltips_za.lua");
		readFromLua("lua/item_tooltips_swp.lua");
		readFromLua("lua/item_tooltips_pvp.lua");
		readFromLua("lua/item_tooltips_darkmoon.lua");
		readFromLua("lua/item_tooltips_sr_greens.lua");
		readGemsFromLua("lua/item_tooltips_gems.lua");
		postProcessData();
	}

	private void readFromLua(String filePath) {
		Globals globals = getSavedVariables(filePath);
		LuaTable savras_savedItems = globals.get("Savras_SavedItems").checktable();

		for (LuaValue key : savras_savedItems.keys()) {
			ItemTooltip itemTooltip = parseItemTooltip(savras_savedItems, key);
			Item item = ItemLuaParser.parseItem(itemTooltip, this);
			if (itemById.containsKey(item.getId())) {
				System.err.printf("Overwriting [%s] (source: %s)%n", item.getName(), filePath);
			}
			itemById.put(item.getId(), item);
			itemByLink.put(item.getItemLink(), item);
			itemByName.computeIfAbsent(item.getName(), x -> new ArrayList<>()).add(item);
		}
	}

	private void readGemsFromLua(String filePath) {
		Globals globals = getSavedVariables(filePath);
		LuaTable savras_savedItems = globals.get("Savras_SavedItems").checktable();

		for (LuaValue key : savras_savedItems.keys()) {
			ItemTooltip itemTooltip = parseItemTooltip(savras_savedItems, key);
			Gem gem = GemLuaParser.parseGem(itemTooltip);
			if (gemById.containsKey(gem.getId())) {
				System.err.printf("Overwriting [%s] (source: %s)%n", gem.getName(), filePath);
			}
			gemById.put(gem.getId(), gem);
			gemByName.put(gem.getName(), gem);
		}
	}

	private void postProcessData() {
		for (ItemSet itemSet : itemSetByName.values()) {
			if (itemSet.getItemSetBonuses() == null) {
				continue;// Ignore obsolete stuff
			}
			for (ItemSetBonus itemSetBonus : itemSet.getItemSetBonuses()) {
				ItemStatParser.resetAll();
				if (ItemStatParser.tryParse(itemSetBonus.getDescription())) {
					itemSetBonus.setBonusStats(ItemStatParser.getParsedStats());
				} else {
					throw new IllegalArgumentException("Missing bonus: " + itemSetBonus.getDescription());
				}
			}
		}

		for (Item item : itemById.values()) {
			item.getTooltip().getSources().stream().filter(Source::isTradedFromToken).forEach(source -> {
				ItemLink tokenLink = source.getTradedFromToken();
				Item sourceItem = itemByLink.get(tokenLink);
				if (sourceItem.getItemType() == null) {
					sourceItem.setItemType(ItemType.Token);
				}
			});
		}

		for (Item item : getAllItems()) {
			ItemSet tierSet = item.getItemSet();
			if (tierSet != null) {
				tierSets.computeIfAbsent(tierSet, x -> new ArrayList<>()).add(item);
			}
		}

		for (Item item : getAllItems()) {
			for (Source source : item.getSources()) {
				ItemLink tokenLink = source.getTradedFromToken();
				if (tokenLink != null) {
					tokenToItems.computeIfAbsent(getItem(tokenLink), x -> new ArrayList<>()).add(item);
				}
			}
		}

		for (Map.Entry<Item, List<Item>> entry : tokenToItems.entrySet()) {
			Item token = entry.getKey();
			List<Item> items = entry.getValue();
			for (Item item : items) {
				itemToTokens.computeIfAbsent(item, x -> new ArrayList<>()).add(token);
			}
		}
	}

	private ItemTooltip parseItemTooltip(LuaTable savras_savedItems, LuaValue key) {
		ItemLink itemLink = ItemLink.parse(key.checkjstring());
		LuaTable record = savras_savedItems.get(key).checktable();
		long order = record.get("order").checklong();
		LuaTable sources = record.get("sources").checktable();
		LuaTable left = record.get("left").checktable();
		LuaTable right = record.get("right").checktable();

		List<String> sourceList = toList(sources);
		List<String> leftList = toList(left);
		List<String> rightList = toList(right);

		Set<Source> sourceSet = sourceList.stream()
				.map(line -> SourceParser.parse(line, pveRepository))
				.collect(Collectors.toCollection(LinkedHashSet::new));

		return new ItemTooltip(itemLink, order, sourceSet, leftList, rightList);
	}

	private static Globals getSavedVariables(String filePath) {
		Globals globals = JsePlatform.standardGlobals();
		LuaValue chunk = globals.loadfile(filePath);
		chunk.call();
		return globals;
	}

	private static List<String> toList(LuaTable left) {
		List<String> result = new ArrayList<>();
		for (int i = 1; i <= left.length(); ++i) {
			result.add(left.get(i).isnil() ? null : left.get(i).checkjstring());
		}
		return result;
	}

	public void addItemSet(ItemSet itemSet) {
		itemSetByName.put(itemSet.getName(), itemSet);
	}

	public void addEnchant(Enchant enchant) {
		enchantById.put(enchant.getId(), enchant);
		enchantByName.put(enchant.getName(), enchant);
	}
}
