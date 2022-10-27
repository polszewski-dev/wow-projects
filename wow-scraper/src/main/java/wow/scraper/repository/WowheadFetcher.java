package wow.scraper.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import wow.commons.model.pve.GameVersion;
import wow.scraper.model.JsonItemDetails;
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

	public static List<JsonItemDetails> fetchItemDetails(GameVersion gameVersion, String urlPart) throws IOException {
		String urlStr = getRootUrlStr(gameVersion) + urlPart;
		String html = fetchPage(urlStr);

		Matcher matcher = ITEM_LIST_PATTERN.matcher(html);

		if (!matcher.find()) {
			throw new IllegalArgumentException("Can't parse item data from: " + urlStr);
		}

		String itemJson = matcher.group(1);

		itemJson = fixJsonErrors(itemJson);

		return MAPPER.readValue(itemJson, new TypeReference<>() {});
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
