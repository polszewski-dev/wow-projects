package wow.commons.model.effect.component;

import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.spell.Coefficient;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-16
 */
public record AbsorptionComponent(
		Coefficient coefficient,
		AttributeCondition condition,
		int min,
		int max
) implements EffectComponent {
	public AbsorptionComponent {
		Objects.requireNonNull(coefficient);
		Objects.requireNonNull(condition);
	}
}
