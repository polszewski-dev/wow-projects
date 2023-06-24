package wow.scraper.fetchers.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.fetchers.PageFetcher;
import wow.scraper.fetchers.WowheadFetcher;
import wow.scraper.model.*;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private static final Pattern BOSS_LIST_PATTERN = Pattern.compile("\"id\":\"npcs\".*?\"data\":(\\[.*]),\"extraCols\":\\[Listview\\.extraCols\\.popularity]");

	private PageFetcher pageFetcher;

	@Override
	public List<JsonItemDetails> fetchItemDetails(GameVersionId gameVersion, String urlPart) throws IOException {
		String json = fetchAndParse(gameVersion, urlPart, ITEM_LIST_PATTERN);

		return MAPPER.readValue(json, new TypeReference<>() {});
	}

	@Override
	public List<JsonSpellDetails> fetchSpellDetails(GameVersionId gameVersion, String urlPart) throws IOException {
		String json = fetchAndParse(gameVersion, urlPart, SPELL_LIST_PATTERN);

		return MAPPER.readValue(json, new TypeReference<>() {});
	}

	@Override
	public List<JsonZoneDetails> fetchZoneDetails(GameVersionId gameVersion, String urlPart) throws IOException {
		String json = fetchAndParse(gameVersion, urlPart, ZONE_LIST_PATTERN);

		return MAPPER.readValue(json, new TypeReference<>() {});
	}

	@Override
	public List<JsonBossDetails> fetchBossDetails(GameVersionId gameVersion, String urlPart) throws IOException {
		String json = fetchAndParse(gameVersion, urlPart, BOSS_LIST_PATTERN);

		return MAPPER.readValue(json, new TypeReference<>() {});
	}

	@Override
	public String fetchRaw(GameVersionId gameVersion, String urlPart) throws IOException {
		String urlStr = getRootUrlStr(gameVersion) + urlPart;

		return pageFetcher.fetchPage(urlStr);
	}

	private String fetchAndParse(GameVersionId gameVersion, String urlPart, Pattern itemListPattern) throws IOException {
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
	public WowheadItemInfo fetchItemTooltip(GameVersionId gameVersion, int id) throws IOException {
		return fetchTooltip(gameVersion, "item", id, WowheadItemInfo.class);
	}

	@Override
	public WowheadSpellInfo fetchSpellTooltip(GameVersionId gameVersion, int id) throws IOException {
		return fetchTooltip(gameVersion, "spell", id, WowheadSpellInfo.class);
	}

	private <T> T fetchTooltip(GameVersionId gameVersion, String type, int id, Class<T> clazz) throws IOException {
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

	private String getTooltipUrlStr(GameVersionId gameVersion, String type, int id) {
		final String urlFormat = "https://nether.wowhead.com/tooltip/%s/%s?dataEnv=%s&locale=0";
		return urlFormat.formatted(type, id, gameVersion.getDataEnv());
	}
}
