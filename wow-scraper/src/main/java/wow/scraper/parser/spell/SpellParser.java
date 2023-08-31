package wow.scraper.parser.spell;

import wow.scraper.parser.scraper.ScraperParser;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public class SpellParser extends ScraperParser<SpellPattern, SpellMatcher, SpellMatcherParams> {
	public SpellParser(List<SpellPattern> patterns) {
		super(patterns);
	}

	@Override
	protected SpellMatcher createMatcher(SpellPattern pattern) {
		return new SpellMatcher(pattern);
	}

	@Override
	protected SpellMatcherParams createMatcherParams(String line) {
		return new SpellMatcherParams(line);
	}
}
