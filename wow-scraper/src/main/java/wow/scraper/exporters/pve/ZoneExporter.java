package wow.scraper.exporters.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonZoneDetails;
import wow.scraper.model.WowheadGameVersion;
import wow.scraper.model.WowheadZoneType;

import java.io.IOException;
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
	protected List<JsonZoneDetails> getData(GameVersionId gameVersion) throws IOException {
		return getZoneDetailRepository().getAll(gameVersion);
	}

	@Override
	protected void fixData(List<JsonZoneDetails> zones) {
		zones.removeIf(x -> getScraperConfig().getIgnoredZoneIds().contains(x.getId()));
		zones.forEach(this::fixType);
		zones.add(creteUnknownZone());
	}

	@Override
	protected Comparator<JsonZoneDetails> getComparator() {
		return Comparator.comparing(JsonZoneDetails::getInstance)
				.thenComparing(JsonZoneDetails::getExpansion)
				.thenComparing(JsonZoneDetails::getName);
	}

	private static JsonZoneDetails creteUnknownZone() {
		JsonZoneDetails unknown = new JsonZoneDetails();
		unknown.setId(0);
		unknown.setName("UNKNOWN");
		unknown.setExpansion(WowheadGameVersion.VANILLA.getCode());
		unknown.setInstance(WowheadZoneType.NORMAL.getCode());
		return unknown;
	}

	private void fixType(JsonZoneDetails zone) {
		if (zone.getName().equals("Onyxia's Lair")) {
			zone.setInstance(WowheadZoneType.RAID.getCode());
		}
	}
}
