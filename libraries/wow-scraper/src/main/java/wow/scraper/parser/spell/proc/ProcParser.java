package wow.scraper.parser.spell.proc;

import wow.scraper.parser.spell.SpellParser;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-09-06
 */
public class ProcParser extends SpellParser<ProcPattern, ProcMatcher, ProcMatcherParams> {
	public ProcParser(List<ProcPattern> patterns) {
		super(patterns);
	}

	@Override
	protected ProcMatcher createMatcher(ProcPattern pattern) {
		return new ProcMatcher(pattern);
	}

	@Override
	protected ProcMatcherParams createMatcherParams(String line) {
		return new ProcMatcherParams(line);
	}
}
