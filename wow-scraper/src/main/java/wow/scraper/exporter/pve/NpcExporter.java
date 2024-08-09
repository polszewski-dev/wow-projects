package wow.scraper.exporter.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.pve.excel.NpcExcelBuilder;
import wow.scraper.model.JsonNpcDetails;

import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class NpcExporter extends PveBaseExporter<JsonNpcDetails, NpcExcelBuilder> {
	@Override
	protected void addHeader(NpcExcelBuilder builder) {
		builder.addNpcHeader();
	}

	@Override
	protected void addRow(JsonNpcDetails npc, NpcExcelBuilder builder) {
		builder.add(npc);
	}

	@Override
	protected List<JsonNpcDetails> getData(GameVersionId gameVersion) {
		return getNpcDetailRepository().getAll(gameVersion);
	}

	@Override
	protected void fixData() {
		data.removeIf(x -> getScraperConfig().getIgnoredNpcIds().contains(x.getId()));
	}

	@Override
	protected Comparator<JsonNpcDetails> getComparator() {
		return Comparator.comparing(JsonNpcDetails::getName);
	}
}
