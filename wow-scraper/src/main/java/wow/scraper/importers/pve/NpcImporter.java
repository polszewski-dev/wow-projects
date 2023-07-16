package wow.scraper.importers.pve;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.importers.parsers.ZoneNpcListParser;
import wow.scraper.model.JsonNpcDetails;
import wow.scraper.model.WowheadZoneType;
import wow.scraper.repository.ZoneDetailRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-26
 */
public class NpcImporter extends PveImporter<JsonNpcDetails> {
	private final ZoneDetailRepository zoneDetailRepository;

	public NpcImporter(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher, ZoneDetailRepository zoneDetailRepository) {
		super(scraperConfig, wowheadFetcher);
		this.zoneDetailRepository = zoneDetailRepository;
	}

	@Override
	protected void doImport(GameVersionId gameVersion) throws IOException {
		importInstanceNpcs(gameVersion);
		importWorldNpcs(gameVersion);
	}

	private void importInstanceNpcs(GameVersionId gameVersion) throws IOException {
		var instances = zoneDetailRepository.getAll(gameVersion, WowheadZoneType.instances());

		for (var instance : instances) {
			String zoneHtml = getWowheadFetcher().fetchRaw(gameVersion, "zone=" + instance.getId());
			List<Integer> npcIds = new ZoneNpcListParser(zoneHtml).parse();
			List<JsonNpcDetails> npcs = getWowheadFetcher().fetchNpcDetails(gameVersion, "npcs", npcIds);

			for (var npc : npcs) {
				var existingNpc = getResult().computeIfAbsent(gameVersion, npc.getId(), x -> {
					npc.setLocation(new ArrayList<>());
					return npc;
				});
				existingNpc.getLocation().add(instance.getId());
			}
		}
	}

	private void importWorldNpcs(GameVersionId gameVersion) throws IOException {
		var npcs = getWowheadFetcher().fetchNpcDetails(gameVersion, "npcs", getScraperConfig().getNpcIdsToFetch());

		for (var npc : npcs) {
			assertNotPresent(gameVersion, npc);
			saveDetails(gameVersion, npc.getId(), npc);
		}
	}

	private void assertNotPresent(GameVersionId gameVersion, JsonNpcDetails npc) {
		if (getResult().getOptional(gameVersion, npc.getId()).isPresent()) {
			throw new IllegalArgumentException("%s is aldeady present".formatted(npc));
		}
	}
}
