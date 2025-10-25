package wow.scraper.exporter.item.excel;

import lombok.Getter;
import lombok.SneakyThrows;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.parser.tooltip.ItemTooltipParser;

import java.util.ArrayList;
import java.util.List;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.ITEM;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Getter
public class ItemExcelBuilder extends WowExcelBuilder {
	private final ItemSheetWriter itemSheetWriter;

	public ItemExcelBuilder(ScraperConfig config, ScraperDatafixes datafixes) {
		super(config, datafixes);
		this.itemSheetWriter = new ItemSheetWriter(this);
	}

	public void addItemHeader() {
		writeHeader(ITEM, itemSheetWriter, 2, 1);
	}

	@SneakyThrows
	@Override
	public void finish(String fileName) {
		flushItems();
		super.finish(fileName);
	}

	public void add(ItemTooltipParser parser) {
		if (isToBeIgnored(parser.getItemId())) {
			return;
		}
		itemParserQueue.add(parser);
	}

	private boolean isToBeIgnored(int itemId) {
		return datafixes.getIgnoredItemIds().contains(itemId);
	}

	private final List<ItemTooltipParser> itemParserQueue = new ArrayList<>();

	private void flushItems() {
		for (var parser : itemParserQueue) {
			writeItem(parser);
		}
		itemParserQueue.clear();
	}

	private void writeItem(ItemTooltipParser parser) {
		if (parser.hasRandomEnchant()) {
			for (int i = 0; i < parser.getRandomEnchantCount(); ++i) {
				parser.setRandomEnchantIdx(i);
				writeRow(parser, itemSheetWriter);
			}
		} else {
			writeRow(parser, itemSheetWriter);
		}
	}
}
