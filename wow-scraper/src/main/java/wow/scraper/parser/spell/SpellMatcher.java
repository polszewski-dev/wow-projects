package wow.scraper.parser.spell;

import wow.commons.model.Duration;
import wow.commons.model.spell.ResourceType;
import wow.scraper.parser.scraper.ScraperMatcher;

import java.util.Optional;
import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public class SpellMatcher extends ScraperMatcher<SpellPattern, SpellPatternParams, SpellMatcherParams> {
	public SpellMatcher(SpellPattern pattern) {
		super(pattern);
	}

	@Override
	protected String getLineToMatch(SpellMatcherParams params) {
		return params.getLine();
	}

	public Optional<Integer> getMinDmg() {
		return getOptionalInteger(getPatternParams().getMinDmg());
	}

	public Optional<Integer> getMaxDmg() {
		return getOptionalInteger(getPatternParams().getMaxDmg());
	}

	public Optional<Integer> getMinDmg2() {
		return getOptionalInteger(getPatternParams().getMinDmg2());
	}

	public Optional<Integer> getMaxDmg2() {
		return getOptionalInteger(getPatternParams().getMaxDmg2());
	}

	public Optional<Integer> getDotDmg() {
		return getOptionalInteger(getPatternParams().getDotDmg());
	}

	public Optional<Integer> getTickDmg() {
		return getOptionalInteger(getPatternParams().getTickDmg());
	}

	public Optional<Duration> getTickInterval() {
		return getOptionalDuration(getPatternParams().getTickInterval());
	}

	public Optional<Duration> getDotDuration() {
		return getOptionalDuration(getPatternParams().getDotDuration());
	}

	public Optional<Integer> getCostAmount() {
		return getOptionalInteger(getPatternParams().getCostAmount());
	}

	public Optional<ResourceType> getCostType() {
		return getOptional(getPatternParams().getCostType(), ResourceType::parse);
	}

	private Optional<Integer> getOptionalInteger(String pattern) {
		return getOptional(pattern, Integer::valueOf);
	}

	private Optional<Duration> getOptionalDuration(String pattern) {
		return getOptional(pattern, Duration::parse);
	}

	private <T> Optional<T> getOptional(String pattern, Function<String, T> mapper) {
		String value = evalParams(pattern);
		if (value != null && !value.isEmpty()) {
			return Optional.of(value).map(mapper);
		}
		return Optional.empty();
	}
}
