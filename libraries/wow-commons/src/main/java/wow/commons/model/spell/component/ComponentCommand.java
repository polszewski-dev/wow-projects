package wow.commons.model.spell.component;

import wow.commons.model.AnyDuration;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.spell.*;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-11-30
 */
public sealed interface ComponentCommand {
	SpellTarget target();

	default boolean isSingleTarget() {
		return target().isSingle();
	}

	default boolean isAoE() {
		return target().isAoE();
	}

	record DirectCommand(
			SpellTarget target,
			ComponentType type,
			Coefficient coefficient,
			int min,
			int max,
			DirectComponentBonus bonus
	) implements ComponentCommand {
		public DirectCommand {
			Objects.requireNonNull(type);
			Objects.requireNonNull(coefficient);
		}

		public SpellSchool school() {
			return coefficient.school();
		}
	}

	record PeriodicCommand(
			SpellTarget target,
			ComponentType type,
			Coefficient coefficient,
			int amount,
			int numTicks,
			TickScheme tickScheme
	) implements ComponentCommand {
		public PeriodicCommand {
			Objects.requireNonNull(target);
			Objects.requireNonNull(type);
			Objects.requireNonNull(coefficient);
			Objects.requireNonNull(tickScheme);
		}

		public SpellSchool school() {
			return coefficient.school();
		}
	}

	record ApplyEffect(
			SpellTarget target,
			Effect effect,
			AnyDuration duration,
			int numStacks,
			int numCharges,
			EffectReplacementMode replacementMode
	) implements ComponentCommand {
		public ApplyEffect {
			Objects.requireNonNull(target);
			Objects.requireNonNull(effect);
			Objects.requireNonNull(duration);
			Objects.requireNonNull(replacementMode);
		}
	}
}
