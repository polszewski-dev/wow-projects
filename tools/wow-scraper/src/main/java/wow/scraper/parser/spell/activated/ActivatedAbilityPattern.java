package wow.scraper.parser.spell.activated;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.parser.spell.SpellPattern;
import wow.scraper.parser.spell.params.SpellPatternParams;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-09-03
 */
public class ActivatedAbilityPattern extends SpellPattern<SpellPatternParams> {
	public ActivatedAbilityPattern(String pattern, SpellPatternParams params, Set<GameVersionId> requiredVersion) {
		super(pattern, params, requiredVersion);
	}
}
