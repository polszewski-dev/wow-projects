package wow.scraper.parsers.spell;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.scraper.parsers.scraper.ScraperMatcherParams;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
@AllArgsConstructor
@Getter
public class SpellMatcherParams implements ScraperMatcherParams {
	private final String line;

	@Override
	public String getOriginalLine() {
		return line;
	}
}
