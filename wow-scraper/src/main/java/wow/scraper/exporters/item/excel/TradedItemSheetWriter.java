package wow.scraper.exporters.item.excel;

import wow.scraper.parsers.tooltip.TradedItemParser;

import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.REQ_CLASS;
import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.REQ_LEVEL;

/**
 * User: POlszewski
 * Date: 2023-05-18
 */
public class TradedItemSheetWriter extends ItemBaseSheetWriter<TradedItemParser> {
	public TradedItemSheetWriter(ItemBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		writeCommonHeader();
		setHeader(REQ_LEVEL);
		setHeader(REQ_CLASS);
		writeIconAndTooltipHeader();
	}

	@Override
	public void writeRow(TradedItemParser parser) {
		writeCommonColumns(parser);
		setValue(parser.getRequiredLevel());
		setValue(parser.getRequiredClass());
		writeIconAndTooltip(parser);
	}
}
