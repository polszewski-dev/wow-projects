package wow.scraper.parsers.scraper;

import wow.commons.util.parser.ParserUtil;

import java.util.regex.Matcher;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public abstract class ScraperMatcher<P extends ScraperPattern<Q>, Q extends ScraperPatternParams, N extends ScraperMatcherParams> {
	protected final P pattern;
	private String[] parsedValues;
	protected N matcherParams;

	protected ScraperMatcher(P pattern) {
		this.pattern = pattern;
	}

	public boolean tryParse(N params) {
		String lineToMatch = getLineToMatch(params);
		Matcher matcher = pattern.getMatcher(lineToMatch);

		if (matcher == null) {
			return false;
		}

		this.parsedValues = ParserUtil.getMatchedGroups(matcher);
		this.matcherParams = params;
		return true;
	}

	protected abstract String getLineToMatch(N params);

	public boolean hasMatch() {
		return parsedValues != null;
	}

	public int getInt() {
		return getInt(1);
	}

	public double getDouble() {
		return getDouble(1);
	}

	public String getString() {
		return getString(1);
	}

	public int getInt(int i) {
		return Integer.parseInt(getString(i));
	}

	public double getDouble(int i) {
		return Double.parseDouble(getString(i));
	}

	public String getString(int i) {
		if (!hasMatch()) {
			throw new IllegalArgumentException();
		}
		if (i == 0) {
			return matcherParams.getOriginalLine();
		}
		return parsedValues[i - 1];
	}

	protected String evalParams(String pattern) {
		return ParserUtil.substituteParams(pattern, this::getString);
	}

	protected Q getPatternParams() {
		return pattern.getParams();
	}
}
