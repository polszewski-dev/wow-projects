package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.importers.pve.ZoneImporter;
import wow.scraper.model.JsonZoneDetails;
import wow.scraper.model.WowheadZoneType;
import wow.scraper.repository.ZoneDetailRepository;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
@Repository
public class ZoneDetailRepositoryImpl implements ZoneDetailRepository {
	private final ZoneImporter zoneImporter;

	public ZoneDetailRepositoryImpl(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher) {
		this.zoneImporter = new ZoneImporter(scraperConfig, wowheadFetcher);
	}

	@Override
	public List<JsonZoneDetails> getAll(GameVersionId gameVersion) throws IOException {
		return zoneImporter.getList(gameVersion);
	}

	@Override
	public List<JsonZoneDetails> getAll(GameVersionId gameVersion, List<WowheadZoneType> zoneTypes) throws IOException {
		return zoneImporter.getList(gameVersion).stream()
				.filter(x -> zoneTypes.contains(WowheadZoneType.fromCode(x.getInstance())))
				.toList();
	}
}
