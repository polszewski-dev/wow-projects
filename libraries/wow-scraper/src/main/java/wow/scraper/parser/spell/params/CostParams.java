package wow.scraper.parser.spell.params;

import wow.commons.model.spell.Coefficient;
import wow.commons.model.spell.ResourceType;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-09
 */
public record CostParams(
		String amount,
		ResourceType type,
		Coefficient coefficient
) {
	public CostParams {
		Objects.requireNonNull(amount);
		Objects.requireNonNull(type);
	}
}
