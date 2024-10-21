package wow.scraper.parser.stat;

import wow.commons.model.attribute.Attributes;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.profession.ProfessionId;
import wow.scraper.parser.scraper.ScraperMatcher;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class StatMatcher extends ScraperMatcher<StatPattern, StatPatternParams, StatMatcherParams> {
	public StatMatcher(StatPattern pattern) {
		super(pattern);
	}

	public List<ItemType> getParamItemTypes() {
		return getPatternParams().itemTypes();
	}

	public List<ItemSubType> getParamItemSubTypes() {
		return getPatternParams().itemSubTypes();
	}

	public ProfessionId getRequiredProfession() {
		return getPatternParams().requiredProfession();
	}

	public Integer getRequiredProfessionLevel() {
		return getPatternParams().requiredProfessionLevel();
	}

	public List<PveRole> getParamPveRoles() {
		return getPatternParams().pveRoles();
	}

	public Attributes getAttributes() {
		return getAttributes(pattern.getAttributePatterns());
	}
}
