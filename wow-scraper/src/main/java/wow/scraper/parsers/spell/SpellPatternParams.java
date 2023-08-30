package wow.scraper.parsers.spell;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.scraper.parsers.scraper.ScraperPatternParams;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
@AllArgsConstructor
@Getter
public class SpellPatternParams implements ScraperPatternParams {
	private final String minDmg;
	private final String maxDmg;
	private final String minDmg2;
	private final String maxDmg2;
	private final String dotDmg;
	private final String tickDmg;
	private final String tickInterval;
	private final String dotDuration;
	private final String costAmount;
	private final String costType;
}
