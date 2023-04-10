package wow.scraper.parsers;

import lombok.extern.slf4j.Slf4j;
import wow.commons.model.professions.ProfessionId;
import wow.scraper.config.ScraperConfig;
import wow.scraper.model.*;

import java.util.*;
import java.util.function.IntFunction;

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

	private static final SourceOverrides SOURCE_OVERRIDES = new SourceOverrides();

	private final JsonItemDetails itemDetails;
	private final List<WowheadSource> sources;
	private final List<JsonSourceMore> sourceMores;
	private final String requiredFactionName;

	public WowheadSourceParser(JsonItemDetailsAndTooltip itemDetailsAndTooltip, String requiredFactionName) {
		this.itemDetails = itemDetailsAndTooltip.getDetails();
		this.sources = itemDetails.getSources()
				.stream()
				.map(WowheadSource::fromCode)
				.toList();
		this.sourceMores = itemDetails.getSourceMores();
		this.requiredFactionName = requiredFactionName;
	}

	public static void configure(ScraperConfig scraperConfig) {
		SOURCE_OVERRIDES.addSourceOverrides(scraperConfig);
	}

	public List<String> getSource() {
		Set<String> overrides = SOURCE_OVERRIDES.getSources(itemDetails.getId(), itemDetails.getName());

		if (overrides != null) {
			return new ArrayList<>(overrides);
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
			return sourceBossDrop(sourceMore.getN(), sourceMore.getTi(), sourceMore.getZ());
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

	private static String sourceBossDrop(String bossName, Integer bossId, Integer zoneId) {
		return String.format("BossDrop:%s:%s:%s", bossName != null ? bossName : "", bossId != null ? bossId : 0, zoneId != null ? zoneId : 0);
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

	private static class SourceOverrides {
		private final List<String> pvpItemNameParts = new ArrayList<>();
		private final Set<Integer> pvpItemIds = new HashSet<>();
		private final Map<Integer, Set<String>> itemIdToSources = new HashMap<>();

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
			addBossDropOverrides(config.getBossDropOverrides());
			addTradedForOverrides(config.getTokenToTradedFor(), WowheadSourceParser::sourceToken);
			addTradedForOverrides(config.getItemStartingQuestToTradedFor(), WowheadSourceParser::sourceItemStartingQuest);
		}

		private void addWorldDropOverrides(ScraperConfig config) {
			for (Integer itemId : config.getWorldDropOverrides()) {
				getSourceList(itemId).add(SOURCE_WORLD_DROP);
			}
		}

		private void addBossDropOverrides(Map<Integer, List<Integer>> tokenIdToBossId) {
			for (Map.Entry<Integer, List<Integer>> entry : tokenIdToBossId.entrySet()) {
				Integer tokenId = entry.getKey();
				List<Integer> bossIds = entry.getValue();

				for (Integer bossId : bossIds) {
					getSourceList(tokenId).add(sourceBossDrop(null, bossId, null));
				}
			}
		}

		private void addTradedForOverrides(Map<Integer, List<Integer>> tokenIdToItemId, IntFunction<String> sourceCreator) {
			for (Map.Entry<Integer, List<Integer>> entry : tokenIdToItemId.entrySet()) {
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
