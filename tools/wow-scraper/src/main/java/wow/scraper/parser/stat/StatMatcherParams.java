package wow.scraper.parser.stat;

import lombok.Getter;
import wow.scraper.parser.scraper.ScraperMatcherParams;

/**
 * User: POlszewski
 * Date: 2022-12-10
 */
@Getter
public class StatMatcherParams extends ScraperMatcherParams {
	public StatMatcherParams(String line) {
		super(line);
		removeNoise();
	}
}
