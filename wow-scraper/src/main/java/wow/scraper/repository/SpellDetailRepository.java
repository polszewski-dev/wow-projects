package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
public interface SpellDetailRepository {
	boolean hasDetail(GameVersionId gameVersion, WowheadSpellCategory category, int spellId);

	Optional<JsonSpellDetails> getDetail(GameVersionId gameVersion, WowheadSpellCategory category, int spellId) throws IOException;

	void saveDetail(GameVersionId gameVersion, WowheadSpellCategory category, int spellId, JsonSpellDetails spellDetails) throws IOException;

	List<Integer> getSpellIds(GameVersionId gameVersion, WowheadSpellCategory category);
}
