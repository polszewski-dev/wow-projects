package wow.commons.repository.impl.parsers;

import wow.commons.model.attributes.Attributes;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class SocketBonusParser {
	public static Attributes parseSocketBonus(String line) {
		for (StatParser parser : StatParserRepository.getInstance().getSocketBonusStatParsers()) {
			Attributes socketBonus = parser.resetAndTryParse(line);
			if (socketBonus != null) {
				return socketBonus;
			}
		}
		throw new IllegalArgumentException("Invalid socket bonus: " + line);
	}
}
