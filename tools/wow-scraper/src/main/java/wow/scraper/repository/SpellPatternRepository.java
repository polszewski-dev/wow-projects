package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spell.AbilityId;
import wow.scraper.parser.spell.ability.AbilityParser;
import wow.scraper.parser.spell.activated.ActivatedAbilityParser;
import wow.scraper.parser.spell.misc.MiscEffectParser;
import wow.scraper.parser.spell.proc.ProcParser;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public interface SpellPatternRepository {
	AbilityParser getAbilityParser(AbilityId abilityId, GameVersionId gameVersion);

	ActivatedAbilityParser getActivatedAbilityParser(GameVersionId gameVersion);

	ProcParser getProcParser(GameVersionId gameVersion);

	MiscEffectParser getMiscEffectParser(GameVersionId gameVersion);
}
