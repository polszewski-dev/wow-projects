package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.importers.pve.FactionImporter;
import wow.scraper.model.JsonFactionDetails;
import wow.scraper.repository.FactionDetailRepository;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
@Repository
public class FactionDetailRepositoryImpl implements FactionDetailRepository {
	private final FactionImporter factionImporter;

	public FactionDetailRepositoryImpl(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher) {
		this.factionImporter = new FactionImporter(scraperConfig, wowheadFetcher);
	}

	@Override
	public List<JsonFactionDetails> getAll(GameVersionId gameVersion) throws IOException {
		return factionImporter.getList(gameVersion);
	}
}
