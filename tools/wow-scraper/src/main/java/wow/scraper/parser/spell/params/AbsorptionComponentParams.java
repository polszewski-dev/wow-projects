package wow.scraper.parser.spell.params;

import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.spell.Coefficient;
import wow.commons.model.spell.SpellTarget;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-09
 */
public record AbsorptionComponentParams(
		SpellTarget target,
		Coefficient coefficient,
		AttributeCondition condition,
		String min,
		String max
) {
	public AbsorptionComponentParams {
		Objects.requireNonNull(target);
		Objects.requireNonNull(coefficient);
		Objects.requireNonNull(condition);
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);
	}
}
