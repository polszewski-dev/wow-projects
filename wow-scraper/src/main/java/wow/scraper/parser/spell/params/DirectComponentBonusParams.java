package wow.scraper.parser.spell.params;

import wow.commons.model.spell.AbilityId;

/**
 * User: POlszewski
 * Date: 2023-09-30
 */
public record DirectComponentBonusParams(
		String min,
		String max,
		AbilityId requiredEffect
) {
}
