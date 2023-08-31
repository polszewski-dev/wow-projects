package wow.scraper.parser;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.config.ScraperContext;
import wow.scraper.model.*;

import java.util.*;
import java.util.function.IntFunction;

import static wow.scraper.util.CommonAssertions.assertBothAreEqual;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@Slf4j
public class WowheadSourceParser {
	public static final String SOURCE_WORLD_DROP = "WorldDrop";
	public static final String SOURCE_QUESTS = "Quests";
	public static final String SOURCE_BADGES = "Badges";
	public static final String SOURCE_PVP = "PvP";

	private static final Map<GameVersionId, SourceOverrides> SOURCE_OVERRIDES_MAP = new EnumMap<>(GameVersionId.class);
	private static ScraperContext scraperContext;

	private final JsonItemDetails itemDetails;
	private final List<WowheadSource> sources;
	private final List<JsonSourceMore> sourceMores;
	private final String requiredFactionName;
	private final GameVersionId gameVersion;

	public WowheadSourceParser(JsonItemDetails itemDetails, String requiredFactionName, GameVersionId gameVersion) {
		this.itemDetails = itemDetails;
		this.sources = itemDetails.getSources()
				.stream()
				.map(WowheadSource::fromCode)
				.toList();
		this.sourceMores = itemDetails.getSourceMores();
		this.requiredFactionName = requiredFactionName;
		this.gameVersion = gameVersion;
	}

	public static void configure(ScraperContext scraperContext) {
		WowheadSourceParser.scraperContext = scraperContext;

		for (GameVersionId gameVersion : scraperContext.getScraperConfig().getGameVersions()) {
			SOURCE_OVERRIDES_MAP
					.computeIfAbsent(gameVersion, x -> new SourceOverrides(gameVersion))
					.addSourceOverrides(scraperContext.getScraperConfig());
		}
	}

	public List<String> getSource() {
		var overrides = SOURCE_OVERRIDES_MAP
				.get(gameVersion)
				.getSources(itemDetails.getId(), itemDetails.getName());

		if (overrides != null) {
			return List.copyOf(overrides);
		}

		if (requiredFactionName != null) {
			return List.of(sourceFaction(requiredFactionName));
		}

		if (sources.size() != 1) {
			log.error("Multiple sources for: " + getName());
		}

		return switch (sources.get(0)) {
			case CRAFTED -> List.of(parseSingleSourceCrafted());
			case PVP_ARENA -> List.of(parseSingleSourcePvP());
			case BADGES -> List.of(parseSingleBadges());
			case QUEST -> List.of(parseSingleQuest());
			case DROP -> List.of(parseSingleSourceDrop());
			case FISHING -> List.of(parseFishing());
		};
	}

	private String parseSingleSourceDrop() {
		if (hasNoSourceMores()) {
			return SOURCE_WORLD_DROP;
		}

		JsonSourceMore sourceMore = assertSingleSourceMore();

		if (sourceMore.getN() != null) {
			return switch (sourceMore.getT()) {
				case 1 -> sourceNpcDrop(sourceMore.getN(), sourceMore.getTi(), sourceMore.getZ(), gameVersion);
				case 2 -> sourceContainerObject(sourceMore.getN(), sourceMore.getTi(), sourceMore.getZ(), gameVersion);
				case 3 -> sourceContainerItem(sourceMore.getN(), sourceMore.getTi());
				default -> throw new IllegalArgumentException("Unknown type: %s".formatted(sourceMore.getN()));
			};
		}

		return sourceZoneDrop(sourceMore.getZ());
	}

	private String parseSingleSourceCrafted() {
		if (hasNoSourceMores()) {
			throw new IllegalArgumentException("No source more: " + getName());
		}

		JsonSourceMore sourceMore = assertSingleSourceMore();

		return sourceCrafted(WowheadProfession.fromCode(sourceMore.getS()).getProfession());
	}

	private String parseSingleSourcePvP() {
		return SOURCE_PVP;
	}

	private String parseSingleBadges() {
		return SOURCE_BADGES;
	}

	private String parseSingleQuest() {
		if (hasNoSourceMores()) {
			return SOURCE_QUESTS;
		}

		JsonSourceMore sourceMore = assertSingleSourceMore();

		// 2 or more quests
		if (sourceMore.getN() == null) {
			return SOURCE_QUESTS;
		}

		return sourceQuest(sourceMore.getN());
	}

	private String parseFishing() {
		return sourceCrafted(ProfessionId.FISHING);
	}

	@SneakyThrows
	private static String sourceNpcDrop(String npcName, Integer npcId, Integer zoneId, GameVersionId gameVersion) {
		Integer newNpcId = scraperContext.getScraperConfig().getSourceNpcToNpcReplacements().get(npcId);

		if (newNpcId != null) {
			return sourceNpcDrop(null, newNpcId, null, gameVersion);
		}

		var optionalNpc = scraperContext.getNpcDetailRepository().getById(gameVersion, npcId);

		if (optionalNpc.isEmpty()) {
			return "NONE";
		}

		JsonNpcDetails npc = optionalNpc.orElseThrow();

		if (npcName == null) {
			npcName = npc.getName();
		} else {
			assertBothAreEqual("npc.name", npcName, npc.getName());
		}

		return "NpcDrop:%s:%s".formatted(npcName, npcId);
	}

	private String sourceContainerObject(String containerName, Integer containerId, Integer zoneId, GameVersionId gameVersion) {
		Integer newNpcId = scraperContext.getScraperConfig().getSourceObjectToNpcReplacements().get(containerId);

		if (newNpcId != null) {
			return sourceNpcDrop(null, newNpcId, null, gameVersion);
		}

		return "ContainerObject:%s:%s:%s".formatted(containerName, containerId, zoneId);
	}

	private static String sourceContainerItem(String containerName, Integer containerId) {
		return "ContainerItem:%s:%s".formatted(containerName, containerId);
	}

	private static String sourceZoneDrop(Integer zoneId) {
		return "ZoneDrop:" + zoneId;
	}

	private static String sourceCrafted(ProfessionId profession) {
		return "Crafted:" + profession;
	}

	private static String sourceToken(Integer tokenId) {
		return "Token:" + tokenId;
	}

	private static String sourceItemStartingQuest(Integer tokenId) {
		return "ItemStartingQuest:" + tokenId;
	}

	private static String sourceQuest(String questName) {
		return "Quest:" + questName;
	}

	public static String sourceFaction(String requiredFactionName) {
		return "Faction:" + requiredFactionName;
	}

	private String getName() {
		return itemDetails.getName();
	}

	private boolean hasNoSourceMores() {
		return sourceMores == null || sourceMores.isEmpty();
	}

	private JsonSourceMore assertSingleSourceMore() {
		if (sourceMores.size() != 1) {
			log.error("Multiple source mores: " + sourceMores);
		}
		JsonSourceMore sourceMore = sourceMores.get(0);
		fixData(sourceMore);
		return sourceMore;
	}

	private void fixData(JsonSourceMore sourceMore) {
		if ("Onyxia".equals(sourceMore.getN()) && sourceMore.getZ() == null) {
			sourceMore.setZ(2159);
		}
	}

	@RequiredArgsConstructor
	private static class SourceOverrides {
		private final List<String> pvpItemNameParts = new ArrayList<>();
		private final Set<Integer> pvpItemIds = new HashSet<>();
		private final Map<Integer, Set<String>> itemIdToSources = new HashMap<>();

		private final GameVersionId gameVersion;

		public Set<String> getSources(int itemId, String itemName) {
			if (pvpItemIds.contains(itemId) || isPvPItemName(itemName)) {
				return Set.of(SOURCE_PVP);
			}

			return itemIdToSources.get(itemId);
		}

		private boolean isPvPItemName(String name) {
			return pvpItemNameParts.stream().anyMatch(x -> isMatches(name, x));
		}

		private boolean isMatches(String name, String pattern) {
			if (pattern.startsWith("^")) {
				return name.startsWith(pattern.substring(1));
			}
			return name.contains(pattern);
		}

		public void addSourceOverrides(ScraperConfig config) {
			pvpItemNameParts.addAll(config.getPvpItemNameParts());
			pvpItemIds.addAll(config.getPvpItemIds());
			addWorldDropOverrides(config);
			addNpcDropOverrides(config.getNpcDropOverrides());
			addTradedForOverrides(config.getTokenToTradedFor(), WowheadSourceParser::sourceToken);
			addTradedForOverrides(config.getItemStartingQuestToTradedFor(), WowheadSourceParser::sourceItemStartingQuest);
		}

		private void addWorldDropOverrides(ScraperConfig config) {
			for (Integer itemId : config.getWorldDropOverrides()) {
				getSourceList(itemId).add(SOURCE_WORLD_DROP);
			}
		}

		private void addNpcDropOverrides(Map<Integer, List<Integer>> tokenIdToNpcId) {
			for (var entry : tokenIdToNpcId.entrySet()) {
				Integer tokenId = entry.getKey();
				List<Integer> npcIds = entry.getValue();

				for (Integer npcId : npcIds) {
					getSourceList(tokenId).add(sourceNpcDrop(null, npcId, null, gameVersion));
				}
			}
		}

		private void addTradedForOverrides(Map<Integer, List<Integer>> tokenIdToItemId, IntFunction<String> sourceCreator) {
			for (var entry : tokenIdToItemId.entrySet()) {
				Integer tokenId = entry.getKey();
				List<Integer> itemIds = entry.getValue();

				for (Integer itemId : itemIds) {
					getSourceList(itemId).add(sourceCreator.apply(tokenId));
				}
			}
		}

		private Set<String> getSourceList(Integer itemId) {
			return itemIdToSources.computeIfAbsent(itemId, x -> new TreeSet<>());
		}
	}
}
