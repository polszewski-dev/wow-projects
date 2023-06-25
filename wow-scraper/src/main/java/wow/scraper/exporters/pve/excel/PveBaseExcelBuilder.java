package wow.scraper.exporters.pve.excel;

import lombok.Getter;
import wow.scraper.config.ScraperConfig;
import wow.scraper.exporters.excel.WowExcelBuilder;
import wow.scraper.model.JsonBossDetails;
import wow.scraper.model.JsonFactionDetails;
import wow.scraper.model.JsonZoneDetails;

import static wow.commons.repository.impl.parsers.pve.PveBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
@Getter
public class PveBaseExcelBuilder extends WowExcelBuilder {
	private final ZoneSheetWriter zoneSheetWriter;
	private final BossSheetWriter bossSheetWriter;
	private final FactionSheetWriter factionSheetWriter;

	public PveBaseExcelBuilder(ScraperConfig config) {
		super(config);
		this.zoneSheetWriter = new ZoneSheetWriter(this);
		this.bossSheetWriter = new BossSheetWriter(this);
		this.factionSheetWriter = new FactionSheetWriter(this);
	}

	public void addZoneHeader() {
		writeHeader(ZONES, zoneSheetWriter, 0, 1);
	}

	public void add(JsonZoneDetails zone) {
		writeRow(zone, zoneSheetWriter);
	}

	public void addBossHeader() {
		writeHeader(BOSSES, bossSheetWriter, 0, 1);
	}

	public void add(JsonBossDetails boss) {
		writeRow(boss, bossSheetWriter);
	}

	public void addFactionHeader() {
		writeHeader(FACTIONS, factionSheetWriter, 0, 1);
	}

	public void add(JsonFactionDetails faction) {
		writeRow(faction, factionSheetWriter);
	}
}
