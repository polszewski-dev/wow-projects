package wow.scraper.parser.spell.params;

import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.model.spell.SpellTarget;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-10-11
 */
public record StatConversionParams(
		SpellTarget target,
		PrimitiveAttributeId from,
		PrimitiveAttributeId to,
		AttributeCondition toCondition,
		String ratioPct
) {
	public StatConversionParams {
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);
		Objects.requireNonNull(ratioPct);
	}
}
