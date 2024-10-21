package wow.scraper.parser.spell.ability;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.parser.spell.SpellPattern;
import wow.scraper.parser.spell.params.SpellPatternParams;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-09-19
 */
public class AbilityPattern extends SpellPattern<SpellPatternParams> {
	public AbilityPattern(String pattern, SpellPatternParams params, Set<GameVersionId> requiredVersion) {
		super(pattern, params, requiredVersion);
	}
}
