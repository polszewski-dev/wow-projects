package wow.scraper.exporter.item.excel;

import lombok.Getter;
import wow.scraper.config.ScraperConfig;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.parser.tooltip.TradedItemParser;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.TRADE;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Getter
public class TradedItemExcelBuilder extends WowExcelBuilder {
	private final TradedItemSheetWriter tradedItemSheetWriter;

	public TradedItemExcelBuilder(ScraperConfig config) {
		super(config);
		this.tradedItemSheetWriter = new TradedItemSheetWriter(this);
	}

	public void addTradedItemHeader() {
		writeHeader(TRADE, tradedItemSheetWriter, 2, 1);
	}

	public void add(TradedItemParser parser) {
		if (isToBeIgnored(parser.getItemId())) {
			return;
		}
		writeRow(parser, tradedItemSheetWriter);
	}

	private boolean isToBeIgnored(int itemId) {
		return config.getIgnoredItemIds().contains(itemId);
	}
}
