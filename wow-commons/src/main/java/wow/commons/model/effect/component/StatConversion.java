package wow.commons.model.effect.component;

import wow.commons.model.Percent;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.model.attribute.primitive.ValueType;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public record StatConversion(
		PrimitiveAttributeId from,
		PrimitiveAttributeId to,
		AttributeCondition toCondition,
		Percent ratioPct
) implements EffectComponent {
	public StatConversion {
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);
		Objects.requireNonNull(toCondition);
		Objects.requireNonNull(ratioPct);
		if (from.getValueType() != ValueType.POINT) {
			throw new IllegalArgumentException();
		}
	}
}
