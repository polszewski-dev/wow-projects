package wow.scraper.importer.pve;

import lombok.SneakyThrows;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperDatafixes;
import wow.scraper.fetcher.WowheadFetcher;
import wow.scraper.importer.parser.ZoneNpcListParser;
import wow.scraper.model.JsonNpcDetails;
import wow.scraper.model.JsonZoneDetails;
import wow.scraper.model.WowheadNpcClassification;
import wow.scraper.model.WowheadZoneType;
import wow.scraper.repository.ZoneDetailRepository;

import java.util.ArrayList;
import java.util.List;

import static wow.scraper.importer.parser.ZoneNpcListParser.ParseResult;
import static wow.scraper.importer.parser.ZoneNpcListParser.ParseResultType.NPC;

/**
 * User: POlszewski
 * Date: 2023-06-26
 */
public class NpcImporter extends PveImporter<JsonNpcDetails> {
	private final ZoneDetailRepository zoneDetailRepository;

	public NpcImporter(ScraperConfig scraperConfig, ScraperDatafixes scraperDatafixes, WowheadFetcher wowheadFetcher, ZoneDetailRepository zoneDetailRepository) {
		super(scraperConfig, scraperDatafixes, wowheadFetcher);
		this.zoneDetailRepository = zoneDetailRepository;
	}

	@Override
	protected void doImport(GameVersionId gameVersion) {
		importInstanceNpcs(gameVersion);
		importWorldNpcs(gameVersion);
		createPseudoNpcs(gameVersion);
		getResult().values(gameVersion).forEach(this::fixLocation);
	}

	private void importInstanceNpcs(GameVersionId gameVersion) {
		var instances = zoneDetailRepository.getAll(gameVersion, WowheadZoneType.instances());

		for (var instance : instances) {
			importInstanceNpcs(gameVersion, instance);
		}
	}

	private void importInstanceNpcs(GameVersionId gameVersion, JsonZoneDetails instance) {
		var npcs = getInstanceNpcs(gameVersion, instance);

		for (var npc : npcs) {
			var existingNpc = getResult().computeIfAbsent(gameVersion, npc.getId(), x -> {
				npc.setLocation(new ArrayList<>());
				return npc;
			});
			existingNpc.getLocation().add(instance.getId());
		}
	}

	private List<JsonNpcDetails> getInstanceNpcs(GameVersionId gameVersion, JsonZoneDetails instance) {
		String zoneHtml = getWowheadFetcher().fetchRaw(gameVersion, "zone=" + instance.getId());
		List<Integer> npcIds = new ZoneNpcListParser(zoneHtml).parse().stream()
				.filter(x -> x.type() == NPC)
				.map(ParseResult::id)
				.toList();
		return getWowheadFetcher().fetchNpcDetails(gameVersion, "npcs", npcIds);
	}

	private void importWorldNpcs(GameVersionId gameVersion) {
		var npcs = getWowheadFetcher().fetchNpcDetails(gameVersion, "npcs", getScraperDatafixes().getNpcIdsToFetch());

		for (var npc : npcs) {
			assertNotPresent(gameVersion, npc);
			if (npc.getLocation() == null) {
				npc.setLocation(new ArrayList<>());
			}
			saveDetails(gameVersion, npc.getId(), npc);
		}
	}

	private void createPseudoNpcs(GameVersionId gameVersion) {
		getScraperDatafixes().getNpcToCreate().forEach((id, name) -> createPseudoNpc(gameVersion, id, name));
	}

	@SneakyThrows
	private void createPseudoNpc(GameVersionId gameVersion, Integer id, String name) {
		var npc = new JsonNpcDetails();
		npc.setId(id);
		npc.setName(name);
		npc.setClassification(WowheadNpcClassification.BOSS.getCode());
		npc.setBoss(1);

		var zoneId = getZoneId(npc).get(0);
		zoneDetailRepository.getById(gameVersion, zoneId)
				.ifPresent(x -> saveDetails(gameVersion, id, npc));
	}

	private void fixLocation(JsonNpcDetails npc) {
		var location = getZoneId(npc);
		if (location != null) {
			npc.setLocation(location);
		} else {
			npc.getLocation().removeIf(x -> x == -1);
		}
	}

	private List<Integer> getZoneId(JsonNpcDetails npc) {
		return getScraperDatafixes().getNpcLocationOverrides().get(npc.getId());
	}

	private void assertNotPresent(GameVersionId gameVersion, JsonNpcDetails npc) {
		if (getResult().getOptional(gameVersion, npc.getId()).isPresent()) {
			throw new IllegalArgumentException("%s is aldeady present".formatted(npc));
		}
	}
}
