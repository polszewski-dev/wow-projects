package wow.scraper.parser.stat;

import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.profession.ProfessionId;
import wow.scraper.parser.scraper.ScraperPatternParams;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-09-18
 */
public record StatPatternParams(
		List<ItemType> itemTypes,
		List<ItemSubType> itemSubTypes,
		ProfessionId requiredProfession,
		Integer requiredProfessionLevel,
		List<PveRole> pveRoles
) implements ScraperPatternParams {
}
