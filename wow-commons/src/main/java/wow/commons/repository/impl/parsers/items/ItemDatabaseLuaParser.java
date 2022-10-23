package wow.commons.repository.impl.parsers.items;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.*;
import wow.commons.model.sources.Source;
import wow.commons.model.sources.SourceParser;
import wow.commons.model.sources.TradedFromToken;
import wow.commons.repository.PVERepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.repository.impl.parsers.gems.GemLuaParser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-10-22
 */
public class ItemDatabaseLuaParser {
	private final ItemDataRepositoryImpl itemDataRepository;
	private final PVERepository pveRepository;

	private final List<Set<Source>> toFixLater = new ArrayList<>();

	public ItemDatabaseLuaParser(ItemDataRepositoryImpl itemDataRepository, PVERepository pveRepository) {
		this.itemDataRepository = itemDataRepository;
		this.pveRepository = pveRepository;
	}

	public void readFromLua() {
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
			Item item = ItemLuaParser.parseItem(itemTooltip, itemDataRepository);
			if (itemDataRepository.getItem(item.getId()).isPresent()) {
				System.err.printf("Overwriting [%s] (source: %s)%n", item.getName(), filePath);
			}
			itemDataRepository.addItem(item);
		}
	}

	private void readGemsFromLua(String filePath) {
		Globals globals = getSavedVariables(filePath);
		LuaTable savras_savedItems = globals.get("Savras_SavedItems").checktable();

		for (LuaValue key : savras_savedItems.keys()) {
			ItemTooltip itemTooltip = parseItemTooltip(savras_savedItems, key);
			Gem gem = GemLuaParser.parseGem(itemTooltip);
			if (itemDataRepository.getGem(gem.getId()).isPresent()) {
				System.err.printf("Overwriting [%s] (source: %s)%n", gem.getName(), filePath);
			}
			itemDataRepository.addGem(gem);
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

		if (sourceSet.stream().anyMatch(Source::isTradedFromToken)) {
			toFixLater.add(sourceSet);
		}

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

	private void postProcessData() {
		for (var sources : toFixLater) {
			List<Source> newSources = getFixedItemSources(sources);
			sources.clear();
			sources.addAll(newSources);
		}

		processSets();
		processTokens();
	}

	private void processSets() {
		for (ItemSet itemSet : itemDataRepository.getAllItemSets()) {
			if (itemSet.getItemSetBonuses() == null) {
				continue;// Ignore obsolete stuff
			}
			for (ItemSetBonus itemSetBonus : itemSet.getItemSetBonuses()) {
				ItemStatParser statParser = new ItemStatParser();
				if (statParser.tryParse(itemSetBonus.getDescription())) {
					itemSetBonus.setBonusStats(statParser.getParsedStats());
				} else {
					throw new IllegalArgumentException("Missing bonus: " + itemSetBonus.getDescription());
				}
			}
		}

		for (Item item : itemDataRepository.getAllItems()) {
			ItemSet itemSet = item.getItemSet();
			if (itemSet != null) {
				itemDataRepository.addSetItem(item, itemSet);
			}
		}
	}

	private void processTokens() {
		for (Item item : itemDataRepository.getAllItems()) {
			item.getSources().stream().filter(Source::isTradedFromToken).forEach(source -> {
				Item sourceToken = source.getSourceToken();

				if (sourceToken.getItemType() == null) {
					sourceToken.setItemType(ItemType.Token);
				}

				itemDataRepository.addToken(item, sourceToken);
			});
		}
	}

	private List<Source> getFixedItemSources(Set<Source> sources) {
		return sources.stream()
				.map(source -> {
					if (source.isTradedFromToken())	{
						Item token = itemDataRepository.getItem(source.getSourceToken().getId()).orElseThrow();
						return new TradedFromToken(token);
					}
					return source;
				})
				.collect(Collectors.toList());
	}
}
