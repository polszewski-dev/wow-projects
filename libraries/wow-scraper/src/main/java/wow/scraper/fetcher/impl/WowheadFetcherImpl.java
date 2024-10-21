package wow.scraper.fetcher.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.fetcher.PageFetcher;
import wow.scraper.fetcher.WowheadFetcher;
import wow.scraper.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@Component
@AllArgsConstructor
public class WowheadFetcherImpl implements WowheadFetcher {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final Pattern ITEM_LIST_PATTERN = Pattern.compile("var listviewitems = (\\[.*]);");
	private static final Pattern SPELL_LIST_PATTERN = Pattern.compile("var listviewspells = (\\[.*]);");
	private static final Pattern ZONE_LIST_PATTERN = Pattern.compile("id: 'zones'.*?data: (\\[.*])");
	private static final Pattern NPC_LIST_PATTERN = Pattern.compile("\"id\":\"npcs\".*?\"data\":(\\[.*]),\"extraCols\":\\[Listview\\.extraCols\\.popularity]");
	private static final Pattern FACTION_LIST_PATTERN = Pattern.compile("id: 'factions'.*?data:\\s*(\\[.*])");

	private PageFetcher pageFetcher;

	@SneakyThrows
	@Override
	public List<JsonItemDetails> fetchItemDetails(GameVersionId gameVersion, String urlPart) {
		String json = fetchAndParse(gameVersion, urlPart, ITEM_LIST_PATTERN);

		return MAPPER.readValue(json, new TypeReference<>() {});
	}

	@Override
	public List<JsonItemDetails> fetchItemDetails(GameVersionId gameVersion, String urlPart, Collection<Integer> itemIds) {
		return fetchIdFiltered(gameVersion, urlPart, itemIds, "151", this::fetchItemDetails);
	}

	@SneakyThrows
	@Override
	public List<JsonSpellDetails> fetchSpellDetails(GameVersionId gameVersion, String urlPart) {
		String json = fetchAndParse(gameVersion, urlPart, SPELL_LIST_PATTERN);

		return MAPPER.readValue(json, new TypeReference<>() {});
	}

	@Override
	public List<JsonSpellDetails> fetchSpellDetails(GameVersionId gameVersion, String urlPart, Collection<Integer> spellIds) {
		return fetchIdFiltered(gameVersion, urlPart, spellIds, "14", this::fetchSpellDetails);
	}

	@SneakyThrows
	@Override
	public List<JsonZoneDetails> fetchZoneDetails(GameVersionId gameVersion, String urlPart) {
		String json = fetchAndParse(gameVersion, urlPart, ZONE_LIST_PATTERN);

		return MAPPER.readValue(json, new TypeReference<>() {});
	}

	@SneakyThrows
	@Override
	public List<JsonNpcDetails> fetchNpcDetails(GameVersionId gameVersion, String urlPart) {
		String json = fetchAndParse(gameVersion, urlPart, NPC_LIST_PATTERN);

		return MAPPER.readValue(json, new TypeReference<>() {});
	}

	@Override
	public List<JsonNpcDetails> fetchNpcDetails(GameVersionId gameVersion, String urlPart, Collection<Integer> npcIds) {
		return fetchIdFiltered(gameVersion, urlPart, npcIds, "37", this::fetchNpcDetails);
	}

	@SneakyThrows
	@Override
	public List<JsonFactionDetails> fetchFactionDetails(GameVersionId gameVersion, String urlPart) {
		String json = fetchAndParse(gameVersion, urlPart, FACTION_LIST_PATTERN);

		return MAPPER.readValue(json, new TypeReference<>() {});
	}

	@SneakyThrows
	@Override
	public String fetchRaw(GameVersionId gameVersion, String urlPart) {
		String urlStr = getRootUrlStr(gameVersion) + urlPart;

		return pageFetcher.fetchPage(urlStr);
	}

	@SneakyThrows
	private String fetchAndParse(GameVersionId gameVersion, String urlPart, Pattern itemListPattern) {
		String urlStr = getRootUrlStr(gameVersion) + urlPart;
		String html = pageFetcher.fetchPage(urlStr);

		Matcher matcher = itemListPattern.matcher(html);

		if (!matcher.find()) {
			throw new IllegalArgumentException("Can't parse data from: " + urlStr);
		}

		String itemJson = matcher.group(1);

		itemJson = fixJsonErrors(itemJson);
		return itemJson;
	}

	private String fixJsonErrors(String itemJson) {
		itemJson = itemJson.replace(",firstseenpatch:", ",\"firstseenpatch\":");
		itemJson = itemJson.replace(",popularity:", ",\"popularity\":");
		itemJson = itemJson.replace(",quality:", ",\"quality\":");
		itemJson = itemJson.replace(",contentPhase:", ",\"contentPhase\":");
		return itemJson;
	}

	@Override
	public WowheadItemInfo fetchItemTooltip(GameVersionId gameVersion, int id) {
		return fetchTooltip(gameVersion, "item", id, WowheadItemInfo.class);
	}

	@Override
	public WowheadSpellInfo fetchSpellTooltip(GameVersionId gameVersion, int id) {
		return fetchTooltip(gameVersion, "spell", id, WowheadSpellInfo.class);
	}

	@SneakyThrows
	private <T> T fetchTooltip(GameVersionId gameVersion, String type, int id, Class<T> clazz) {
		String urlStr = getTooltipUrlStr(gameVersion, type, id);
		String json = pageFetcher.fetchPage(urlStr);
		return MAPPER.readValue(json, clazz);
	}

	private String getRootUrlStr(GameVersionId gameVersion) {
		return switch (gameVersion) {
			case VANILLA -> "https://www.wowhead.com/classic/";
			case TBC -> "https://www.wowhead.com/tbc/";
			case WOTLK -> "https://www.wowhead.com/wotlk/";
		};
	}

	private int getDataEnv(GameVersionId gameVersion) {
		return switch (gameVersion) {
			case VANILLA -> 4;
			case TBC -> 5;
			case WOTLK -> 6;
		};
	}

	private String getTooltipUrlStr(GameVersionId gameVersion, String type, int id) {
		final String urlFormat = "https://nether.wowhead.com/tooltip/%s/%s?dataEnv=%s&locale=0";
		return urlFormat.formatted(type, id, getDataEnv(gameVersion));
	}

	@FunctionalInterface
	private interface Fetcher<D> {
		List<D> fetch(GameVersionId gameVersion, String urlPart);
	}

	private <D> List<D> fetchIdFiltered(GameVersionId gameVersion, String urlPart, Collection<Integer> ids, String type, Fetcher<D> fetcher) {
		List<Integer> orderedIds = ids.stream()
				.distinct()
				.sorted()
				.toList();

		List<D> result = new ArrayList<>();

		for (var idChunk : getChunks(orderedIds, 5)) {
			try {
				result.addAll(fetcher.fetch(gameVersion, urlPart + getIdQuery(type, idChunk)));
			} catch (IllegalArgumentException e) {
				// IGNORE: empty result has been returned
			}
		}

		return result;
	}

	// https://www.wowhead.com/classic/items?filter-any=151:151;3:3;20926:20927

	private static <T> List<List<T>> getChunks(List<T> list, int chunkSize) {
		if (list.isEmpty()) {
			return List.of();
		}
		return IntStream.rangeClosed(0, (list.size() - 1) / chunkSize)
				.mapToObj(x -> list.subList(x * chunkSize, Math.min((x + 1) * chunkSize, list.size())))
				.toList();
	}

	private String getIdQuery(String typeId, List<Integer> ids) {
		return Stream.of(
				ids.stream().map(x -> typeId).collect(Collectors.joining(":")),
				ids.stream().map(x -> "3").collect(Collectors.joining(":")),
				ids.stream().map(Object::toString).collect(Collectors.joining(":"))
		).collect(Collectors.joining(";", "?filter-any=", ""));
	}
}
