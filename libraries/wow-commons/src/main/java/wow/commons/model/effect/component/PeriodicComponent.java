package wow.commons.model.effect.component;

import wow.commons.model.spell.Coefficient;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.spell.SpellTarget;
import wow.commons.model.spell.TickScheme;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-16
 */
public record PeriodicComponent(
		SpellTarget target,
		ComponentType type,
		Coefficient coefficient,
		int amount,
		int numTicks,
		TickScheme tickScheme
) implements EffectComponent {
	public PeriodicComponent {
		Objects.requireNonNull(target);
		Objects.requireNonNull(type);
		Objects.requireNonNull(coefficient);
		Objects.requireNonNull(tickScheme);
	}

	public SpellSchool school() {
		return coefficient.school();
	}
}
