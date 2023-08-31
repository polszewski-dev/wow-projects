package wow.scraper.exporter.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonZoneDetails;

import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class ZoneExporter extends PveBaseExporter<JsonZoneDetails> {
	@Override
	protected void addHeader() {
		builder.addZoneHeader();
	}

	@Override
	protected void addRow(JsonZoneDetails zone) {
		builder.add(zone);
	}

	@Override
	protected List<JsonZoneDetails> getData(GameVersionId gameVersion) {
		return getZoneDetailRepository().getAll(gameVersion);
	}

	@Override
	protected void fixData(List<JsonZoneDetails> zones) {
		zones.removeIf(x -> getScraperConfig().getIgnoredZoneIds().contains(x.getId()));
	}

	@Override
	protected Comparator<JsonZoneDetails> getComparator() {
		return Comparator.comparing(JsonZoneDetails::getInstance)
				.thenComparing(JsonZoneDetails::getExpansion)
				.thenComparing(JsonZoneDetails::getName);
	}
}
