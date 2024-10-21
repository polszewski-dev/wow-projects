package wow.scraper.parser.spell.activated;

import wow.scraper.parser.spell.SpellParser;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-09-03
 */
public class ActivatedAbilityParser extends SpellParser<ActivatedAbilityPattern, ActivatedAbilityMatcher, ActivatedAbilityMatcherParams> {
	public ActivatedAbilityParser(List<ActivatedAbilityPattern> patterns) {
		super(patterns);
	}

	@Override
	protected ActivatedAbilityMatcher createMatcher(ActivatedAbilityPattern pattern) {
		return new ActivatedAbilityMatcher(pattern);
	}

	@Override
	protected ActivatedAbilityMatcherParams createMatcherParams(String line) {
		return new ActivatedAbilityMatcherParams(line);
	}
}
