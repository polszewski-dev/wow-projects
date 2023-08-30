package wow.scraper.exporters.spell.excel;

import wow.scraper.parsers.tooltip.TalentTooltipParser;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class TalentRankSheetWriter extends SpellBaseSheetWriter<TalentTooltipParser> {
	public TalentRankSheetWriter(SpellBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader("talent", 40);
		setHeader("rank");
		setHeader("req_version");
		setHeader("tree");
		setHeader("school");
		setHeader("spell");
		setHeader("pet");
		setHeader("stat1");
		setHeader("amount1");
		setHeader("stat2");
		setHeader("amount2");
		setHeader("stat3");
		setHeader("amount3");
		setHeader("stat4");
		setHeader("amount4");
		setHeader("tooltip", 120);
	}

	@Override
	public void writeRow(TalentTooltipParser parser) {
		setValue(parser.getName());
		setValue(parser.getRank());
		setValue(parser.getGameVersion());
		setValue("");//tree
		setValue("");//school
		setValue("");//spell
		setValue("");//pet
		setValue("");//stat1
		setValue("");//amount1
		setValue("");//stat2
		setValue("");//amount2
		setValue("");//stat3
		setValue("");//amount3
		setValue("");//stat4
		setValue("");//amount4
		setValue(parser.getDescription());
	}
}
