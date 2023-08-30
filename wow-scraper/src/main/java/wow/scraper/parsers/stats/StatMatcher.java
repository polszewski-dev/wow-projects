package wow.scraper.parsers.stats;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.professions.ProfessionId;
import wow.commons.repository.impl.parsers.excel.mapper.ComplexAttributeMapper;
import wow.commons.util.AttributesBuilder;
import wow.scraper.parsers.scraper.ScraperMatcher;
import wow.scraper.parsers.setters.StatSetter;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class StatMatcher extends ScraperMatcher<StatPattern, StatPatternParams, StatMatcherParams> {
	public StatMatcher(StatPattern pattern) {
		super(pattern);
	}

	public String getParamType() {
		return getPatternParams().getType();
	}

	public Attributes getParamStats() {
		String value = evalParams(getPatternParams().getAmount());
		double amount = Double.parseDouble(value);
		return getPatternParams().getStatsSupplier().getAttributes(amount);
	}

	public Duration getParamDuration() {
		String value = evalParams(getPatternParams().getDuration());
		return Duration.parse(value);
	}

	public Duration getParamCooldown() {
		return matcherParams.getParsedCooldown();
	}

	public Percent getParamProcChance() {
		return matcherParams.getParsedProcChance();
	}

	public Duration getParamProcCooldown() {
		Duration cooldown = matcherParams.getParsedProcCooldown();
		return cooldown != null ? cooldown : Duration.ZERO;
	}

	public ComplexAttribute getExpression() {
		String value = evalParams(getPatternParams().getExpression());
		return ComplexAttributeMapper.fromString(value);
	}

	public List<ItemType> getParamItemTypes() {
		return getPatternParams().getItemTypes();
	}

	public List<ItemSubType> getParamItemSubTypes() {
		return getPatternParams().getItemSubTypes();
	}

	public ProfessionId getRequiredProfession() {
		return getPatternParams().getRequiredProfession();
	}

	public Integer getRequiredProfessionLevel() {
		return getPatternParams().getRequiredProfessionLevel();
	}

	public List<PveRole> getParamPveRoles() {
		return getPatternParams().getPveRoles();
	}

	public void setStat(AttributesBuilder stats) {
		if (hasMatch()) {
			for (StatSetter setter : pattern.getSetters()) {
				setter.set(stats, this);
			}
		}
	}

	@Override
	protected String getLineToMatch(StatMatcherParams params) {
		return pattern.isLiteral() ? params.getOriginalLine() : params.getLine();
	}

	@Override
	public String toString() {
		return matcherParams != null ? matcherParams.getOriginalLine() : null;
	}
}
