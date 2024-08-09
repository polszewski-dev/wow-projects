package wow.scraper.exporter.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.pve.excel.ZoneExcelBuilder;
import wow.scraper.model.JsonZoneDetails;

import java.util.Comparator;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class ZoneExporter extends PveBaseExporter<JsonZoneDetails, ZoneExcelBuilder> {
	@Override
	protected void addHeader(ZoneExcelBuilder builder) {
		builder.addZoneHeader();
	}

	@Override
	protected void addRow(JsonZoneDetails zone, ZoneExcelBuilder builder) {
		builder.add(zone);
	}

	@Override
	protected List<JsonZoneDetails> getData(GameVersionId gameVersion) {
		return getZoneDetailRepository().getAll(gameVersion);
	}

	@Override
	protected void fixData() {
		data.removeIf(x -> getScraperConfig().getIgnoredZoneIds().contains(x.getId()));
	}

	@Override
	protected Comparator<JsonZoneDetails> getComparator() {
		return Comparator.comparing(JsonZoneDetails::getInstance)
				.thenComparing(JsonZoneDetails::getExpansion)
				.thenComparing(JsonZoneDetails::getName);
	}
}
