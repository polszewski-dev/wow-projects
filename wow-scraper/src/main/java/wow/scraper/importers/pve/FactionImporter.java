package wow.scraper.importers.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.model.JsonFactionDetails;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2023-06-26
 */
public class FactionImporter extends PveImporter<JsonFactionDetails> {
	public FactionImporter(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher) {
		super(scraperConfig, wowheadFetcher);
	}

	@Override
	protected void doImport(GameVersionId gameVersion) throws IOException {
		var factions = getWowheadFetcher().fetchFactionDetails(gameVersion, "factions");

		for (var faction : factions) {
			saveDetails(gameVersion, faction.getId(), faction);
		}
	}
}
