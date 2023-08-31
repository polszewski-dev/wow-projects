package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.SpellId;
import wow.scraper.parser.spell.SpellParser;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public interface SpellPatternRepository {
	SpellParser getSpellParser(SpellId spellId, GameVersionId gameVersion);
}
