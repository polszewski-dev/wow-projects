package wow.scraper.parser.spell;

import lombok.Getter;
import wow.scraper.parser.scraper.ScraperMatcherParams;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
@Getter
public abstract class SpellMatcherParams extends ScraperMatcherParams {
	protected SpellMatcherParams(String line) {
		super(line);
	}
}
