package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.importers.pve.BossImporter;
import wow.scraper.model.JsonBossDetails;
import wow.scraper.repository.BossDetailRepository;
import wow.scraper.repository.ZoneDetailRepository;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
@Repository
public class BossDetailRepositoryImpl implements BossDetailRepository {
	private final BossImporter bossImporter;

	public BossDetailRepositoryImpl(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher, ZoneDetailRepository zoneDetailRepository) {
		this.bossImporter = new BossImporter(scraperConfig, wowheadFetcher, zoneDetailRepository);
	}

	@Override
	public List<JsonBossDetails> getAll(GameVersionId gameVersion) throws IOException {
		return bossImporter.getList(gameVersion);
	}
}
