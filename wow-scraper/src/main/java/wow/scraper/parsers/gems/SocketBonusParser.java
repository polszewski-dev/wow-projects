package wow.scraper.parsers.gems;

import wow.commons.model.attributes.Attributes;
import wow.scraper.parsers.stats.StatParser;
import wow.scraper.parsers.stats.StatPatternRepository;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public final class SocketBonusParser {
	public static Attributes tryParseSocketBonus(String line) {
		StatParser parser = StatPatternRepository.getInstance().getSocketBonusStatParser();
		return parser.tryParseSingleStat(line);
	}

	private SocketBonusParser() {}
}
