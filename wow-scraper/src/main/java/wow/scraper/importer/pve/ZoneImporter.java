package wow.scraper.importer.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.fetcher.WowheadFetcher;
import wow.scraper.model.JsonZoneDetails;
import wow.scraper.model.WowheadZoneType;

/**
 * User: POlszewski
 * Date: 2023-06-26
 */
public class ZoneImporter extends PveImporter<JsonZoneDetails> {
	public ZoneImporter(ScraperConfig scraperConfig, ScraperDatafixes datafixes, WowheadFetcher wowheadFetcher) {
		super(scraperConfig, datafixes, wowheadFetcher);
	}

	@Override
	protected void doImport(GameVersionId gameVersion) {
		var zones = getWowheadFetcher().fetchZoneDetails(gameVersion, "zones");

		for (var zone : zones) {
			fixType(zone);
			saveDetails(gameVersion, zone.getId(), zone);
		}
	}

	private void fixType(JsonZoneDetails zone) {
		WowheadZoneType zoneType = getScraperDatafixes().getZoneTypeOverrides().get(zone.getId());

		if (zoneType != null) {
			zone.setInstance(zoneType.getCode());
		}
	}
}
