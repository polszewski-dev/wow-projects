package wow.scraper.parser.stat;

import lombok.Getter;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.parser.scraper.ScraperPattern;
import wow.scraper.parser.spell.params.AttributePattern;

import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-10-23
 */
@Getter
public class StatPattern extends ScraperPattern<StatPatternParams> {
	private final List<AttributePattern> attributePatterns;

	public StatPattern(String pattern, List<AttributePattern> attributePatterns, StatPatternParams params, Set<GameVersionId> requiredVersion) {
		super(pattern, params, requiredVersion);
		this.attributePatterns = attributePatterns;
	}
}
