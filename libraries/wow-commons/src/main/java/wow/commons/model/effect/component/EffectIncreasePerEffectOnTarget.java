package wow.commons.model.effect.component;

import wow.commons.model.Percent;
import wow.commons.model.attribute.condition.AttributeCondition;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public record EffectIncreasePerEffectOnTarget(
		AttributeCondition condition,
		Percent value,
		Percent max
) implements EffectComponent {
	public EffectIncreasePerEffectOnTarget {
		Objects.requireNonNull(condition);
		Objects.requireNonNull(value);
		Objects.requireNonNull(max);
	}
}
