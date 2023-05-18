package wow.scraper.exporters.item.excel;

import wow.scraper.parsers.tooltip.GemTooltipParser;

import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.*;
import static wow.commons.repository.impl.parsers.items.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-05-18
 */
public class GemSheetWriter extends WowExcelSheetWriter<GemTooltipParser> {
	public GemSheetWriter(ItemBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		writeCommonHeader();
		setHeader(REQ_PROFESSION);
		setHeader(REQ_PROFESSION_LEVEL);
		setHeader(REQ_SIDE);
		setHeader(PVE_ROLES);
		setHeader(GEM_COLOR);
		writeAttributeHeader("", GEM_MAX_STATS);
		setHeader(GEM_META_ENABLERS, 20);
		writeIconAndTooltipHeader();
		writer.nextRow().freeze(2, 1);
	}

	@Override
	public void writeRow(GemTooltipParser parser) {
		writeCommonColumns(parser);
		setValue(parser.getRequiredProfession());
		setValue(parser.getRequiredProfessionLevel());
		setValue(getRequiredSide(parser));
		writePveRoles(parser.getStats());
		setValue(parser.getColor());
		writeAttributes(parser.getStats(), GEM_MAX_STATS);
		setValue(parser.getMetaEnablers());
		writeIconAndTooltip(parser);
		writer.nextRow();
	}
}
