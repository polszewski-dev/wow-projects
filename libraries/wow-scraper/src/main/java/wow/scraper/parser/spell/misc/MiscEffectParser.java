package wow.scraper.parser.spell.misc;

import wow.scraper.parser.spell.SpellParser;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-09-27
 */
public class MiscEffectParser extends SpellParser<MiscEffectPattern, MiscEffectMatcher, MiscEffectMatcherParams> {
	public MiscEffectParser(List<MiscEffectPattern> patterns) {
		super(patterns);
	}

	@Override
	protected MiscEffectMatcher createMatcher(MiscEffectPattern pattern) {
		return new MiscEffectMatcher(pattern);
	}

	@Override
	protected MiscEffectMatcherParams createMatcherParams(String line) {
		return new MiscEffectMatcherParams(line);
	}
}
