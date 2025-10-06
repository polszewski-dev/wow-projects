package wow.commons.model.effect.component;

import wow.commons.model.Percent;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.ValueType;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public record StatConversion(
		AttributeId from,
		AttributeId to,
		StatConversionCondition toCondition,
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
