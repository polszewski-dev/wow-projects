package wow.scraper.exporter.pve.excel;

import lombok.Getter;
import wow.scraper.config.ScraperConfig;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.model.JsonFactionDetails;
import wow.scraper.model.JsonNpcDetails;
import wow.scraper.model.JsonZoneDetails;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
@Getter
public class PveBaseExcelBuilder extends WowExcelBuilder {
	private final ZoneSheetWriter zoneSheetWriter;
	private final NpcSheetWriter npcSheetWriter;
	private final FactionSheetWriter factionSheetWriter;

	public PveBaseExcelBuilder(ScraperConfig config) {
		super(config);
		this.zoneSheetWriter = new ZoneSheetWriter(this);
		this.npcSheetWriter = new NpcSheetWriter(this);
		this.factionSheetWriter = new FactionSheetWriter(this);
	}

	public void addZoneHeader() {
		writeHeader(ZONES, zoneSheetWriter, 0, 1);
	}

	public void add(JsonZoneDetails zone) {
		writeRow(zone, zoneSheetWriter);
	}

	public void addNpcHeader() {
		writeHeader(NPCS, npcSheetWriter, 0, 1);
	}

	public void add(JsonNpcDetails npc) {
		writeRow(npc, npcSheetWriter);
	}

	public void addFactionHeader() {
		writeHeader(FACTIONS, factionSheetWriter, 0, 1);
	}

	public void add(JsonFactionDetails faction) {
		writeRow(faction, factionSheetWriter);
	}
}
