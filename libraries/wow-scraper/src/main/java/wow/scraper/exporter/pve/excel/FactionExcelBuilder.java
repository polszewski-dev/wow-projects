package wow.scraper.exporter.pve.excel;

import lombok.Getter;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.model.JsonFactionDetails;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelSheetNames.FACTIONS;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
@Getter
public class FactionExcelBuilder extends WowExcelBuilder {
	private final FactionSheetWriter factionSheetWriter;

	public FactionExcelBuilder(ScraperConfig config, ScraperDatafixes datafixes) {
		super(config, datafixes);
		this.factionSheetWriter = new FactionSheetWriter(this);
	}

	public void addFactionHeader() {
		writeHeader(FACTIONS, factionSheetWriter, 0, 1);
	}

	public void add(JsonFactionDetails faction) {
		writeRow(faction, factionSheetWriter);
	}
}
