package wow.scraper.fetchers;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.*;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
public interface WowheadFetcher {
	List<JsonItemDetails> fetchItemDetails(GameVersionId gameVersion, String urlPart) throws IOException;

	List<JsonSpellDetails> fetchSpellDetails(GameVersionId gameVersion, String urlPart) throws IOException;

	List<JsonZoneDetails> fetchZoneDetails(GameVersionId gameVersion, String urlPart) throws IOException;

	List<JsonBossDetails> fetchBossDetails(GameVersionId gameVersion, String urlPart) throws IOException;

	String fetchRaw(GameVersionId gameVersion, String urlPart) throws IOException;

	WowheadItemInfo fetchItemTooltip(GameVersionId gameVersion, int id) throws IOException;

	WowheadSpellInfo fetchSpellTooltip(GameVersionId gameVersion, int id) throws IOException;
}
