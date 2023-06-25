package wow.scraper.importers.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.importers.parsers.ZoneBossListParser;
import wow.scraper.model.JsonBossDetails;
import wow.scraper.model.WowheadZoneType;
import wow.scraper.repository.ZoneDetailRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-26
 */
public class BossImporter extends PveImporter<JsonBossDetails> {
	private final ZoneDetailRepository zoneDetailRepository;

	public BossImporter(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher, ZoneDetailRepository zoneDetailRepository) {
		super(scraperConfig, wowheadFetcher);
		this.zoneDetailRepository = zoneDetailRepository;
	}

	@Override
	protected void doImport(GameVersionId gameVersion) throws IOException {
		importInstanceBosses(gameVersion);
		importWorldBosses(gameVersion);
	}

	private void importInstanceBosses(GameVersionId gameVersion) throws IOException {
		var instances = zoneDetailRepository.getAll(gameVersion, WowheadZoneType.instances());

		for (var instance : instances) {
			String zoneHtml = getWowheadFetcher().fetchRaw(gameVersion, "zone=" + instance.getId());
			List<Integer> bossIds = new ZoneBossListParser(zoneHtml).parse();
			List<JsonBossDetails> bosses = getWowheadFetcher().fetchBossDetails(gameVersion, "npcs", bossIds);

			for (var boss : bosses) {
				var existingBoss = getResult().computeIfAbsent(gameVersion, boss.getId(), x -> {
					boss.setLocation(new ArrayList<>());
					return boss;
				});
				existingBoss.getLocation().add(instance.getId());
			}
		}
	}

	private void importWorldBosses(GameVersionId gameVersion) throws IOException {
		var bosses = getWowheadFetcher().fetchBossDetails(gameVersion, "npcs", getScraperConfig().getBossIdsToFetch());

		for (var boss : bosses) {
			assertNotPresent(gameVersion, boss);
			saveDetails(gameVersion, boss.getId(), boss);
		}
	}

	private void assertNotPresent(GameVersionId gameVersion, JsonBossDetails boss) {
		if (getResult().getOptional(gameVersion, boss.getId()).isPresent()) {
			throw new IllegalArgumentException("%s is aldeady present".formatted(boss));
		}
	}
}
