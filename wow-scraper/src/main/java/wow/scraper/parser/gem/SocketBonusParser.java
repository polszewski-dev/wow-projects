package wow.scraper.parser.gem;

import lombok.AllArgsConstructor;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.parser.stat.StatParser;
import wow.scraper.repository.StatPatternRepository;

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
