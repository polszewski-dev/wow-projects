package wow.scraper.exporter.spell.excel;

import wow.scraper.parser.tooltip.TalentTooltipParser;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class TalentSheetWriter extends SpellBaseSheetWriter<TalentTooltipParser> {
	public TalentSheetWriter(SpellBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader("talent", 40);
		setHeader("req_class");
		setHeader("req_version");
		setHeader("tree");
		setHeader("max rank");
		setHeader("talent calculator position");
		setHeader("icon");
	}

	@Override
	public void writeRow(TalentTooltipParser parser) {
		setValue(parser.getName());
		setValue(parser.getCharacterClass());
		setValue(parser.getGameVersion());
		setValue(parser.getTalentTree().getName().toLowerCase());
		setValue(parser.getMaxRank());
		setValue(parser.getTalentCalculatorPosition());
		setValue(parser.getIcon());
	}
}
