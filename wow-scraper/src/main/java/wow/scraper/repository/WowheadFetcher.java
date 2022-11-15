package wow.scraper.repository;

import wow.commons.model.pve.GameVersion;
import wow.scraper.model.JsonBossDetails;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.JsonZoneDetails;
import wow.scraper.model.WowheadItemInfo;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
public interface WowheadFetcher {
	List<JsonItemDetails> fetchItemDetails(GameVersion gameVersion, String urlPart) throws IOException;

	List<JsonZoneDetails> fetchZoneDetails(GameVersion gameVersion, String urlPart) throws IOException;

	List<JsonBossDetails> fetchBossDetails(GameVersion gameVersion, String urlPart) throws IOException;

	WowheadItemInfo fetchTooltip(GameVersion gameVersion, int itemId) throws IOException;
}
