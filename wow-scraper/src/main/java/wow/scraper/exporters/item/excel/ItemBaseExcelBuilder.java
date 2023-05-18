package wow.scraper.exporters.item.excel;

import lombok.Getter;
import polszewski.excel.writer.ExcelWriter;
import wow.scraper.config.ScraperConfig;
import wow.scraper.parsers.tooltip.GemTooltipParser;
import wow.scraper.parsers.tooltip.ItemTooltipParser;
import wow.scraper.parsers.tooltip.TradedItemParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Getter
public class ItemBaseExcelBuilder extends ExcelCellWriter {
	private final ScraperConfig config;

	private final SavedSets savedSets = new SavedSets();

	private final TradedItemSheetWriter tradedItemSheetWriter;
	private final ItemSheetWriter itemSheetWriter;
	private final GemSheetWriter gemSheetWriter;
	private final ItemSetSheetWriter itemSetSheetWriter;

	public ItemBaseExcelBuilder(ScraperConfig config) {
		super(new ExcelWriter());
		this.config = config;
		this.tradedItemSheetWriter = new TradedItemSheetWriter(this);
		this.itemSheetWriter = new ItemSheetWriter(this);
		this.gemSheetWriter = new GemSheetWriter(this);
		this.itemSetSheetWriter = new ItemSetSheetWriter(this);
	}

	public void start() {
		writer.open();
	}

	public void addTradedItemHeader() {
		writer.nextSheet(TRADE);
		tradedItemSheetWriter.writeHeader();
	}

	public void addItemHeader() {
		writer.nextSheet(ITEM);
		itemSheetWriter.writeHeader();
	}

	public void addGemHeader() {
		flushItems();
		writer.nextSheet(GEM);
		gemSheetWriter.writeHeader();
	}

	public void finish(String fileName) throws IOException {
		writer.nextSheet(SET);
		itemSetSheetWriter.writeHeader();
		savedSets.forEach(itemSetSheetWriter::writeRow);
		writer.save(fileName);
	}

	public void add(TradedItemParser parser) {
		if (isToBeIgnored(parser.getItemId())) {
			return;
		}
		tradedItemSheetWriter.writeRow(parser);
	}

	public void add(ItemTooltipParser parser) {
		if (isToBeIgnored(parser.getItemId()) || parser.isRandomEnchantment()) {
			return;
		}
		itemParserQueue.add(parser);
		savedSets.save(parser);
	}

	public void add(GemTooltipParser parser) {
		if (isToBeIgnored(parser.getItemId())) {
			return;
		}
		gemSheetWriter.writeRow(parser);
	}

	private boolean isToBeIgnored(int itemId) {
		return config.getIgnoredItemIds().contains(itemId);
	}

	private final List<ItemTooltipParser> itemParserQueue = new ArrayList<>();

	private void flushItems() {
		for (ItemTooltipParser parser : itemParserQueue) {
			itemSheetWriter.writeRow(parser);
		}
		itemParserQueue.clear();
	}
}
