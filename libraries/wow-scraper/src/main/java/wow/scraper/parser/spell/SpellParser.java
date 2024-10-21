package wow.scraper.parser.spell;

import wow.scraper.parser.scraper.ScraperParser;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public abstract class SpellParser<P extends SpellPattern<?>, M extends SpellMatcher<P, ?, N>, N extends SpellMatcherParams> extends ScraperParser<P, M, N> {
	protected SpellParser(List<P> patterns) {
		super(patterns);
	}
}
