package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetcher.WowheadFetcher;
import wow.scraper.importer.pve.NpcImporter;
import wow.scraper.model.JsonNpcDetails;
import wow.scraper.repository.NpcDetailRepository;
import wow.scraper.repository.ZoneDetailRepository;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
@Repository
public class NpcDetailRepositoryImpl implements NpcDetailRepository {
	private final NpcImporter npcImporter;

	public NpcDetailRepositoryImpl(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher, ZoneDetailRepository zoneDetailRepository) {
		this.npcImporter = new NpcImporter(scraperConfig, wowheadFetcher, zoneDetailRepository);
	}

	@Override
	public List<JsonNpcDetails> getAll(GameVersionId gameVersion) {
		return npcImporter.getList(gameVersion);
	}

	@Override
	public Optional<JsonNpcDetails> getById(GameVersionId gameVersion, int id) {
		return npcImporter.getById(gameVersion, id);
	}
}
