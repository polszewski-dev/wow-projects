package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spells.SpellId;
import wow.scraper.parsers.spell.SpellParser;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public interface SpellPatternRepository {
	SpellParser getSpellParser(SpellId spellId, GameVersionId gameVersion);
}
