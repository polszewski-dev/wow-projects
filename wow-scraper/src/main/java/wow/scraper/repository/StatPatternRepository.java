package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.parser.stat.StatParser;

/**
 * User: POlszewski
 * Date: 2023-07-21
 */
public interface StatPatternRepository {
	StatParser getItemStatParser(GameVersionId gameVersion);

	StatParser getEnchantStatParser(GameVersionId gameVersion);

	StatParser getGemStatParser(GameVersionId gameVersion);

	StatParser getSocketBonusStatParser(GameVersionId gameVersion);
}
