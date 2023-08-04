package wow.scraper.exporters.item.excel;

import lombok.Getter;
import lombok.SneakyThrows;
import wow.scraper.config.ScraperConfig;
import wow.scraper.exporters.excel.WowExcelBuilder;
import wow.scraper.parsers.tooltip.EnchantTooltipParser;
import wow.scraper.parsers.tooltip.GemTooltipParser;
import wow.scraper.parsers.tooltip.ItemTooltipParser;
import wow.scraper.parsers.tooltip.TradedItemParser;

import java.util.ArrayList;
import java.util.List;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Getter
public class ItemBaseExcelBuilder extends WowExcelBuilder {
	private final SavedSets savedSets = new SavedSets();

	private final TradedItemSheetWriter tradedItemSheetWriter;
	private final ItemSheetWriter itemSheetWriter;
	private final EnchantSheetWriter enchantSheetWriter;
	private final GemSheetWriter gemSheetWriter;
	private final ItemSetSheetWriter itemSetSheetWriter;

	public ItemBaseExcelBuilder(ScraperConfig config) {
		super(config);
		this.tradedItemSheetWriter = new TradedItemSheetWriter(this);
		this.itemSheetWriter = new ItemSheetWriter(this);
		this.enchantSheetWriter = new EnchantSheetWriter(this);
		this.gemSheetWriter = new GemSheetWriter(this);
		this.itemSetSheetWriter = new ItemSetSheetWriter(this);
	}

	public void addTradedItemHeader() {
		writeHeader(TRADE, tradedItemSheetWriter, 2, 1);
	}

	public void addItemHeader() {
		writeHeader(ITEM, itemSheetWriter, 2, 1);
	}

	public void addEnchantHeader() {
		flushItems();
		writeHeader(ENCHANT, enchantSheetWriter, 2, 1);
	}

	public void addGemHeader() {
		writeHeader(GEM, gemSheetWriter, 2, 1);
	}

	@SneakyThrows
	@Override
	public void finish(String fileName) {
		writeHeader(SET, itemSetSheetWriter, 1, 1);
		savedSets.forEach(setInfo -> writeRow(setInfo, itemSetSheetWriter));
		super.finish(fileName);
	}

	public void add(TradedItemParser parser) {
		if (isToBeIgnored(parser.getItemId())) {
			return;
		}
		writeRow(parser, tradedItemSheetWriter);
	}

	public void add(ItemTooltipParser parser) {
		if (isToBeIgnored(parser.getItemId()) || parser.isRandomEnchantment()) {
			return;
		}
		itemParserQueue.add(parser);
		savedSets.save(parser);
	}

	public void add(EnchantTooltipParser parser) {
		if (isSpellToBeIgnored(parser.getSpellId())) {
			return;
		}
		writeRow(parser, enchantSheetWriter);
	}

	public void add(GemTooltipParser parser) {
		if (isToBeIgnored(parser.getItemId())) {
			return;
		}
		writeRow(parser, gemSheetWriter);
	}

	private boolean isToBeIgnored(int itemId) {
		return config.getIgnoredItemIds().contains(itemId);
	}

	private boolean isSpellToBeIgnored(int spellId) {
		return config.getIgnoredSpellIds().contains(spellId);
	}

	private final List<ItemTooltipParser> itemParserQueue = new ArrayList<>();

	private void flushItems() {
		for (ItemTooltipParser parser : itemParserQueue) {
			writeRow(parser, itemSheetWriter);
		}
		itemParserQueue.clear();
	}
}
