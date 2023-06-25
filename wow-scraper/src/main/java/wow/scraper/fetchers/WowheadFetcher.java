package wow.scraper.fetchers;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
public interface WowheadFetcher {
	List<JsonItemDetails> fetchItemDetails(GameVersionId gameVersion, String urlPart) throws IOException;

	List<JsonItemDetails> fetchItemDetails(GameVersionId gameVersion, String urlPart, Collection<Integer> itemIds) throws IOException;

	List<JsonSpellDetails> fetchSpellDetails(GameVersionId gameVersion, String urlPart) throws IOException;

	List<JsonZoneDetails> fetchZoneDetails(GameVersionId gameVersion, String urlPart) throws IOException;

	List<JsonBossDetails> fetchBossDetails(GameVersionId gameVersion, String urlPart) throws IOException;

	List<JsonBossDetails> fetchBossDetails(GameVersionId gameVersion, String urlPart, Collection<Integer> bossIds) throws IOException;

	List<JsonFactionDetails> fetchFactionDetails(GameVersionId gameVersion, String urlPart) throws IOException;

	String fetchRaw(GameVersionId gameVersion, String urlPart) throws IOException;

	WowheadItemInfo fetchItemTooltip(GameVersionId gameVersion, int id) throws IOException;

	WowheadSpellInfo fetchSpellTooltip(GameVersionId gameVersion, int id) throws IOException;
}
