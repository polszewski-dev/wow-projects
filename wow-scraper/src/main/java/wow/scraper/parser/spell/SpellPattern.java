package wow.scraper.parser.spell;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.parser.scraper.ScraperPattern;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public class SpellPattern extends ScraperPattern<SpellPatternParams> {
	public SpellPattern(String pattern, SpellPatternParams params, Set<GameVersionId> requiredVersion) {
		super(pattern, params, requiredVersion);
	}
}
