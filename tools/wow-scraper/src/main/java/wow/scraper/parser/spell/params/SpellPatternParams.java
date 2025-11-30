package wow.scraper.parser.spell.params;

import wow.commons.model.spell.AbilityCategory;
import wow.commons.model.spell.AbilityId;
import wow.scraper.parser.scraper.ScraperPatternParams;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public record SpellPatternParams(
		AbilityCategory abilityCategory,
		CastParams cast,
		CostParams cost,
		AbilityId effectRemovedOnHit,
		List<DirectComponentParams> directComponents,
		EffectApplicationParams effectApplication
) implements ScraperPatternParams {
	public SpellPatternParams {
		Objects.requireNonNull(directComponents);
	}
}
