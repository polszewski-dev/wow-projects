package wow.scraper.exporters.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonFactionDetails;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class FactionExporter extends PveBaseExporter<JsonFactionDetails> {
	@Override
	protected void addHeader() {
		builder.addFactionHeader();
	}

	@Override
	protected void addRow(JsonFactionDetails faction) {
		builder.add(faction);
	}

	@Override
	protected List<JsonFactionDetails> getData(GameVersionId gameVersion) throws IOException {
		return getFactionDetailRepository().getAll(gameVersion);
	}

	@Override
	protected void fixData(List<JsonFactionDetails> factions) {
		// VOID
	}

	@Override
	protected Comparator<JsonFactionDetails> getComparator() {
		return Comparator.comparing(JsonFactionDetails::getExpansion)
				.thenComparing(JsonFactionDetails::getName);
	}
}
