package wow.scraper.parsers.gems;

import lombok.AllArgsConstructor;
import wow.commons.model.attributes.Attributes;
import wow.scraper.parsers.stats.StatParser;
import wow.scraper.parsers.stats.StatPatternRepository;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
@AllArgsConstructor
public final class SocketBonusParser {
	private StatPatternRepository statPatternRepository;

	public Attributes tryParseSocketBonus(String line) {
		StatParser parser = statPatternRepository.getSocketBonusStatParser();
		return parser.tryParseSingleStat(line);
	}

	private SocketBonusParser() {}
}
