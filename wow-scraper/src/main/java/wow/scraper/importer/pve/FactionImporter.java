package wow.scraper.importer.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetcher.WowheadFetcher;
import wow.scraper.model.JsonFactionDetails;

/**
 * User: POlszewski
 * Date: 2023-06-26
 */
public class FactionImporter extends PveImporter<JsonFactionDetails> {
	public FactionImporter(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher) {
		super(scraperConfig, wowheadFetcher);
	}

	@Override
	protected void doImport(GameVersionId gameVersion) {
		var factions = getWowheadFetcher().fetchFactionDetails(gameVersion, "factions");

		for (var faction : factions) {
			saveDetails(gameVersion, faction.getId(), faction);
		}
	}
}
