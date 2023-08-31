package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonSpellDetails;
import wow.scraper.model.WowheadSpellCategory;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
public interface SpellDetailRepository {
	Optional<JsonSpellDetails> getDetail(GameVersionId gameVersion, WowheadSpellCategory category, int spellId);

	List<Integer> getDetailIds(GameVersionId gameVersion, WowheadSpellCategory category);

	Optional<JsonSpellDetails> getEnchantDetail(GameVersionId gameVersion, int enchantId);

	List<Integer> getEnchantDetailIds(GameVersionId gameVersion);
}
