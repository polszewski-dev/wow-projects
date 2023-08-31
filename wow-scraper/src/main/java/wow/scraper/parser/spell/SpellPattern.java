package wow.scraper.parser.spell;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.parser.scraper.ScraperPattern;
import wow.scraper.parser.scraper.ScraperPatternParams;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public abstract class SpellPattern<Q extends ScraperPatternParams> extends ScraperPattern<Q> {
	protected SpellPattern(String pattern, Q params, Set<GameVersionId> requiredVersion) {
		super(pattern, params, requiredVersion);
	}
}
