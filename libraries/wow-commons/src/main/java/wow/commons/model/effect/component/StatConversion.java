package wow.commons.model.effect.component;

import wow.commons.model.Percent;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.AttributeTarget;
import wow.commons.model.attribute.ValueType;

import java.util.Objects;

import static wow.commons.model.attribute.AttributeTarget.OWNER;
import static wow.commons.model.attribute.AttributeTarget.PET;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public record StatConversion(
		AttributeTarget fromTarget,
		AttributeId from,
		AttributeId to,
		StatConversionCondition toCondition,
		Percent ratioPct
) implements EffectComponent {
	public StatConversion {
		Objects.requireNonNull(fromTarget);
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);
		Objects.requireNonNull(toCondition);
		Objects.requireNonNull(ratioPct);

		if (fromTarget != OWNER && fromTarget != PET) {
			throw new IllegalArgumentException();
		}

		if (from.getValueType() != ValueType.POINT) {
			throw new IllegalArgumentException();
		}
	}
}
