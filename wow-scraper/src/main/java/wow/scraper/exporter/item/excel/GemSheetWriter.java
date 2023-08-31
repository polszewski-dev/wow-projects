package wow.scraper.exporter.item.excel;

import wow.scraper.parser.tooltip.GemTooltipParser;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.*;
import static wow.commons.repository.impl.parser.item.ItemBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-05-18
 */
public class GemSheetWriter extends ItemBaseSheetWriter<GemTooltipParser> {
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
		writeEffectHeader(GEM_EFFECT_PREFIX, GEM_MAX_EFFECTS);
		setHeader(GEM_META_ENABLERS, 20);
		writeIconAndTooltipHeader();
	}

	@Override
	public void writeRow(GemTooltipParser parser) {
		writeCommonColumns(parser);
		setValue(parser.getRequiredProfession());
		setValue(parser.getRequiredProfessionLevel());
		setValue(getRequiredSide(parser));
		writePveRoles(parser.getEffects(), null, parser.getItemId());
		setValue(parser.getColor());
		writeEffects(parser.getEffects(), GEM_MAX_EFFECTS);
		setValue(parser.getMetaEnablers());
		writeIconAndTooltip(parser);
	}
}
