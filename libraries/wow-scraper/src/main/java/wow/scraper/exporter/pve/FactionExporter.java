package wow.scraper.exporter.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.pve.excel.FactionExcelBuilder;
import wow.scraper.model.JsonFactionDetails;

import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class FactionExporter extends PveBaseExporter<JsonFactionDetails, FactionExcelBuilder> {
	@Override
	protected void addHeader(FactionExcelBuilder builder) {
		builder.addFactionHeader();
	}

	@Override
	protected void addRow(JsonFactionDetails faction, FactionExcelBuilder builder) {
		builder.add(faction);
	}

	@Override
	protected List<JsonFactionDetails> getData(GameVersionId gameVersion) {
		return getFactionDetailRepository().getAll(gameVersion);
	}

	@Override
	protected void fixData() {
		// VOID
	}

	@Override
	protected Comparator<JsonFactionDetails> getComparator() {
		return Comparator.comparing(JsonFactionDetails::getExpansion)
				.thenComparing(JsonFactionDetails::getName);
	}
}
