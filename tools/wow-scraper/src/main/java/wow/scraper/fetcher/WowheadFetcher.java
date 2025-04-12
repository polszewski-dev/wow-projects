package wow.scraper.fetcher;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.*;

import java.util.Collection;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
public interface WowheadFetcher {
	List<JsonItemDetails> fetchItemDetails(GameVersionId gameVersion, String urlPart);

	List<JsonItemDetails> fetchItemDetails(GameVersionId gameVersion, String urlPart, Collection<Integer> itemIds);

	List<JsonSpellDetails> fetchSpellDetails(GameVersionId gameVersion, String urlPart);

	List<JsonSpellDetails> fetchSpellDetails(GameVersionId gameVersion, String urlPart, Collection<Integer> spellIds);

	List<JsonZoneDetails> fetchZoneDetails(GameVersionId gameVersion, String urlPart);

	List<JsonNpcDetails> fetchNpcDetails(GameVersionId gameVersion, String urlPart);

	List<JsonNpcDetails> fetchNpcDetails(GameVersionId gameVersion, String urlPart, Collection<Integer> npcIds);

	List<JsonFactionDetails> fetchFactionDetails(GameVersionId gameVersion, String urlPart);

	List<JsonQuestRewardInfo> fetchQuestRewardInfo(GameVersionId gameVersion, String urlPart);

	String fetchRaw(GameVersionId gameVersion, String urlPart);

	WowheadItemInfo fetchItemTooltip(GameVersionId gameVersion, int id);

	WowheadSpellInfo fetchSpellTooltip(GameVersionId gameVersion, int id);
}
