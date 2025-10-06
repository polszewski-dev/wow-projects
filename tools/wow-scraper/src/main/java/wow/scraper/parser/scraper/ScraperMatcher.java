package wow.scraper.parser.scraper;

import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.util.AttributesParser;
import wow.commons.util.parser.ParserUtil;
import wow.scraper.parser.spell.params.AttributePattern;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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

	protected String getLineToMatch(N params) {
		return params.getLine();
	}

	public String getOriginalLine() {
		return matcherParams != null ? matcherParams.getOriginalLine() : null;
	}

	public boolean hasMatch() {
		return parsedValues != null;
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

	protected Optional<Integer> getOptionalInteger(String pattern) {
		return getOptional(pattern, Integer::valueOf);
	}

	protected Optional<Double> getOptionalDouble(String pattern) {
		return getOptional(pattern, Double::valueOf);
	}

	protected Optional<Duration> getOptionalDuration(String pattern) {
		return getOptional(pattern, Duration::parse);
	}

	protected Optional<AnyDuration> getOptionalAnyDuration(String pattern) {
		return getOptional(pattern, AnyDuration::parse);
	}

	protected Optional<Percent> getOptionalPercent(String pattern) {
		return getOptional(pattern, Percent::parse);
	}

	protected  <T> Optional<T> getOptional(String pattern, Function<String, T> mapper) {
		String value = evalParams(pattern);
		if (value != null && !value.isEmpty()) {
			return Optional.of(value).map(mapper);
		}
		return Optional.empty();
	}

	protected Attributes getAttributes(List<AttributePattern> attributes) {
		var list = attributes.stream()
				.map(this::getAttribute)
				.toList();
		return Attributes.of(list);
	}

	private Attribute getAttribute(AttributePattern attributePattern) {
		var id = evalParams(attributePattern.id());
		var value = getOptionalDouble(attributePattern.value()).orElseThrow();

		return AttributesParser.parse(id, value);
	}

	@Override
	public String toString() {
		return getOriginalLine();
	}
}
