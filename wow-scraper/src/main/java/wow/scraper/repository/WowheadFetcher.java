package wow.scraper.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import wow.commons.model.pve.GameVersion;
import wow.scraper.model.JsonBossDetails;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.JsonZoneDetails;
import wow.scraper.model.WowheadItemInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
public class WowheadFetcher {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final Pattern ITEM_LIST_PATTERN = Pattern.compile("var listviewitems = (\\[.*?]);");
	private static final Pattern ZONE_LIST_PATTERN = Pattern.compile("id: 'zones'.*?data: (\\[.*?])");
	private static final Pattern BOSS_LIST_PATTERN = Pattern.compile("\"id\":\"npcs\".*?\"data\":(\\[.*?]),\"extraCols\":\\[Listview\\.extraCols\\.popularity]");

	public static List<JsonItemDetails> fetchItemDetails(GameVersion gameVersion, String urlPart) throws IOException {
		String json = fetchAndParse(gameVersion, urlPart, ITEM_LIST_PATTERN);

		return MAPPER.readValue(json, new TypeReference<>() {});
	}

	public static List<JsonZoneDetails> fetchZoneDetails(GameVersion gameVersion, String urlPart) throws IOException {
		String json = fetchAndParse(gameVersion, urlPart, ZONE_LIST_PATTERN);

		return MAPPER.readValue(json, new TypeReference<>() {});
	}

	public static List<JsonBossDetails> fetchBossDetails(GameVersion gameVersion, String urlPart) throws IOException {
		String json = fetchAndParse(gameVersion, urlPart, BOSS_LIST_PATTERN);

		return MAPPER.readValue(json, new TypeReference<>() {});
	}

	private static String fetchAndParse(GameVersion gameVersion, String urlPart, Pattern itemListPattern) throws IOException {
		String urlStr = getRootUrlStr(gameVersion) + urlPart;
		String html = fetchPage(urlStr);

		Matcher matcher = itemListPattern.matcher(html);

		if (!matcher.find()) {
			throw new IllegalArgumentException("Can't parse data from: " + urlStr);
		}

		String itemJson = matcher.group(1);

		itemJson = fixJsonErrors(itemJson);
		return itemJson;
	}


	private static String fetchPage(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		return new BufferedReader(
				new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))
				.lines()
				.collect(Collectors.joining("\n"));
	}

	private static String fixJsonErrors(String itemJson) {
		itemJson = itemJson.replace(",firstseenpatch:", ",\"firstseenpatch\":");
		itemJson = itemJson.replace(",popularity:", ",\"popularity\":");
		itemJson = itemJson.replace(",contentPhase:", ",\"contentPhase\":");
		return itemJson;
	}

	public static WowheadItemInfo fetchTooltip(GameVersion gameVersion, int itemId) throws IOException {
		String urlStr = getTooltipUrlStr(gameVersion, itemId);
		URL url = new URL(urlStr);
		return MAPPER.readValue(url, WowheadItemInfo.class);
	}

	private static String getRootUrlStr(GameVersion gameVersion) {
		switch (gameVersion) {
			case Vanilla:
				return "https://www.wowhead.com/classic/";
			case TBC:
				return "https://www.wowhead.com/tbc/";
			case WotLK:
				return "https://www.wowhead.com/wotlk/";
			default:
				throw new IllegalArgumentException("Unhandled game version: " + gameVersion);
		}
	}

	private static String getTooltipUrlStr(GameVersion gameVersion, int itemId) {
		String urlStr;

		switch (gameVersion) {
			case Vanilla:
				urlStr = "https://nether.wowhead.com/tooltip/item/%s?dataEnv=4&locale=0";
				break;
			case TBC:
				urlStr = "https://nether.wowhead.com/tooltip/item/%s?dataEnv=5&locale=0";
				break;
			case WotLK:
				urlStr = "https://nether.wowhead.com/tooltip/item/%s?dataEnv=6&locale=0";
				break;
			default:
				throw new IllegalArgumentException("Unhandled game version: " + gameVersion);
		}

		return String.format(urlStr, itemId);
	}
}
