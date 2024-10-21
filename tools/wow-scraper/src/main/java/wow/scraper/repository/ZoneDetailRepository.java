package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonZoneDetails;
import wow.scraper.model.WowheadZoneType;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
public interface ZoneDetailRepository {
	List<JsonZoneDetails> getAll(GameVersionId gameVersion);

	List<JsonZoneDetails> getAll(GameVersionId gameVersion, List<WowheadZoneType> zoneTypes);

	Optional<JsonZoneDetails> getById(GameVersionId gameVersion, int id);
}
