package wow.commons.model.effect.component;

import wow.commons.model.Percent;
import wow.commons.model.attribute.AttributeId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public record StatConversion(
		StatConversionType type,
		Percent ratioPct
) implements EffectComponent {
	public StatConversion {
		Objects.requireNonNull(type);
		Objects.requireNonNull(ratioPct);
	}

	public AttributeId to() {
		return type.getTo();
	}
}
