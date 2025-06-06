package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.fetcher.WowheadFetcher;
import wow.scraper.importer.pve.FactionImporter;
import wow.scraper.model.JsonFactionDetails;
import wow.scraper.repository.FactionDetailRepository;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
@Repository
public class FactionDetailRepositoryImpl implements FactionDetailRepository {
	private final FactionImporter factionImporter;

	public FactionDetailRepositoryImpl(ScraperConfig scraperConfig, ScraperDatafixes scraperDatafixes, WowheadFetcher wowheadFetcher) {
		this.factionImporter = new FactionImporter(scraperConfig, scraperDatafixes, wowheadFetcher);
	}

	@Override
	public List<JsonFactionDetails> getAll(GameVersionId gameVersion) {
		return factionImporter.getList(gameVersion);
	}
}
