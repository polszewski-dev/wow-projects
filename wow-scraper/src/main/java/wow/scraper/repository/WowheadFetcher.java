package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
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
	List<JsonItemDetails> fetchItemDetails(GameVersionId gameVersion, String urlPart) throws IOException;

	List<JsonZoneDetails> fetchZoneDetails(GameVersionId gameVersion, String urlPart) throws IOException;

	List<JsonBossDetails> fetchBossDetails(GameVersionId gameVersion, String urlPart) throws IOException;

	String fetchRaw(GameVersionId gameVersion, String urlPart) throws IOException;

	WowheadItemInfo fetchItemTooltip(GameVersionId gameVersion, int id) throws IOException;
}
