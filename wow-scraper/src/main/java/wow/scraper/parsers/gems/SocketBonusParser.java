package wow.scraper.parsers.gems;

import lombok.AllArgsConstructor;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.parsers.stats.StatParser;
import wow.scraper.parsers.stats.StatPatternRepository;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
@AllArgsConstructor
public final class SocketBonusParser {
	private final StatPatternRepository statPatternRepository;
	private final GameVersionId gameVersion;

	public Attributes tryParseSocketBonus(String line) {
		StatParser parser = statPatternRepository.getSocketBonusStatParser(gameVersion);
		return parser.tryParseSingleStat(line);
	}
}
