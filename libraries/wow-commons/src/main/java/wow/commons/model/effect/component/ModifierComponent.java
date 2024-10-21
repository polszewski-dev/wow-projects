package wow.commons.model.effect.component;

import wow.commons.model.attribute.Attributes;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-16
 */
public record ModifierComponent(
		Attributes attributes
) implements EffectComponent {
	public ModifierComponent {
		Objects.requireNonNull(attributes);
	}
}
