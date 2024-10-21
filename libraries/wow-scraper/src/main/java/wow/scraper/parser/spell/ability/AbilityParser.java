package wow.scraper.parser.spell.ability;

import wow.scraper.parser.spell.SpellParser;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-09-19
 */
public class AbilityParser extends SpellParser<AbilityPattern, AbilityMatcher, AbilityMatcherParams> {
	public AbilityParser(List<AbilityPattern> patterns) {
		super(patterns);
	}

	@Override
	protected AbilityMatcher createMatcher(AbilityPattern pattern) {
		return new AbilityMatcher(pattern);
	}

	@Override
	protected AbilityMatcherParams createMatcherParams(String line) {
		return new AbilityMatcherParams(line);
	}
}
