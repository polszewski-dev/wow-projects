package wow.scraper.exporter.item.excel;

import lombok.Getter;
import lombok.SneakyThrows;
import wow.scraper.config.ScraperConfig;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.parser.tooltip.ItemTooltipParser;

import java.util.ArrayList;
import java.util.List;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.ITEM;
import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.SET;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Getter
public class ItemExcelBuilder extends WowExcelBuilder {
	private final SavedSets savedSets = new SavedSets();

	private final ItemSheetWriter itemSheetWriter;
	private final ItemSetSheetWriter itemSetSheetWriter;

	public ItemExcelBuilder(ScraperConfig config) {
		super(config);
		this.itemSheetWriter = new ItemSheetWriter(this);
		this.itemSetSheetWriter = new ItemSetSheetWriter(this);
	}

	public void addItemHeader() {
		writeHeader(ITEM, itemSheetWriter, 2, 1);
	}

	@SneakyThrows
	@Override
	public void finish(String fileName) {
		flushItems();
		writeHeader(SET, itemSetSheetWriter, 1, 1);
		savedSets.forEach(setInfo -> writeRow(setInfo, itemSetSheetWriter));
		super.finish(fileName);
	}

	public void add(ItemTooltipParser parser) {
		if (isToBeIgnored(parser.getItemId()) || parser.isRandomEnchantment()) {
			return;
		}
		itemParserQueue.add(parser);
		savedSets.save(parser);
	}

	private boolean isToBeIgnored(int itemId) {
		return config.getIgnoredItemIds().contains(itemId);
	}

	private final List<ItemTooltipParser> itemParserQueue = new ArrayList<>();

	private void flushItems() {
		for (ItemTooltipParser parser : itemParserQueue) {
			writeRow(parser, itemSheetWriter);
		}
		itemParserQueue.clear();
	}
}
