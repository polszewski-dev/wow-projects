package wow.scraper.exporter.spell.excel;

import wow.scraper.config.ScraperConfig;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.parser.tooltip.TalentTooltipParser;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelSheetNames.TALENTS;

/**
 * User: POlszewski
 * Date: 2023-08-24
 */
public class TalentExcelBuilder extends WowExcelBuilder {
	private final TalentSheetWriter talentSheetWriter;

	private String currentName;

	public TalentExcelBuilder(ScraperConfig config) {
		super(config);
		this.talentSheetWriter = new TalentSheetWriter(this);
	}

	public void addTalentHeader() {
		writeHeader(TALENTS, talentSheetWriter, 2, 1);
		resetSeparators();
	}

	public void addTalent(TalentTooltipParser parser) {
		separateByName(parser.getName());
		writeRow(parser, talentSheetWriter);
	}

	private void resetSeparators() {
		currentName = null;
	}

	private void separateByName(String name) {
		if (currentName != null && !name.equals(currentName)) {
			writer.nextRow();
		}
		currentName = name;
	}
}
