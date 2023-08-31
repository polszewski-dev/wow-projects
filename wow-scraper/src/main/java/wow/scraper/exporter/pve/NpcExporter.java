package wow.scraper.exporter.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.pve.excel.PveBaseExcelBuilder;
import wow.scraper.model.JsonNpcDetails;

import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class NpcExporter extends PveBaseExporter<JsonNpcDetails> {
	@Override
	protected void addHeader(PveBaseExcelBuilder builder) {
		builder.addNpcHeader();
	}

	@Override
	protected void addRow(JsonNpcDetails npc, PveBaseExcelBuilder builder) {
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
