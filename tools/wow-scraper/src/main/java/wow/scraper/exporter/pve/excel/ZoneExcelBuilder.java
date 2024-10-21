package wow.scraper.exporter.pve.excel;

import lombok.Getter;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.model.JsonZoneDetails;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelSheetNames.ZONES;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
@Getter
public class ZoneExcelBuilder extends WowExcelBuilder {
	private final ZoneSheetWriter zoneSheetWriter;

	public ZoneExcelBuilder(ScraperConfig config, ScraperDatafixes datafixes) {
		super(config, datafixes);
		this.zoneSheetWriter = new ZoneSheetWriter(this);
	}

	public void addZoneHeader() {
		writeHeader(ZONES, zoneSheetWriter, 0, 1);
	}

	public void add(JsonZoneDetails zone) {
		writeRow(zone, zoneSheetWriter);
	}
}
