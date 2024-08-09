package wow.scraper.exporter.pve.excel;

import lombok.Getter;
import wow.scraper.config.ScraperConfig;
import wow.scraper.exporter.excel.WowExcelBuilder;
import wow.scraper.model.JsonNpcDetails;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelSheetNames.NPCS;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
@Getter
public class NpcExcelBuilder extends WowExcelBuilder {
	private final NpcSheetWriter npcSheetWriter;

	public NpcExcelBuilder(ScraperConfig config) {
		super(config);
		this.npcSheetWriter = new NpcSheetWriter(this);
	}

	public void addNpcHeader() {
		writeHeader(NPCS, npcSheetWriter, 0, 1);
	}

	public void add(JsonNpcDetails npc) {
		writeRow(npc, npcSheetWriter);
	}
}
