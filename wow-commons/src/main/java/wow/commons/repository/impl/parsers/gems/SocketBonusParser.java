package wow.commons.repository.impl.parsers.gems;

import wow.commons.model.attributes.Attributes;
import wow.commons.repository.impl.parsers.stats.StatParser;
import wow.commons.repository.impl.parsers.stats.StatPatternRepository;

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
