package wow.scraper.importers.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.model.JsonZoneDetails;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2023-06-26
 */
public class ZoneImporter extends PveImporter<JsonZoneDetails> {
	public ZoneImporter(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher) {
		super(scraperConfig, wowheadFetcher);
	}

	@Override
	protected void doImport(GameVersionId gameVersion) throws IOException {
		var zones = getWowheadFetcher().fetchZoneDetails(gameVersion, "zones");

		for (var zone : zones) {
			saveDetails(gameVersion, zone.getId(), zone);
		}
	}
}