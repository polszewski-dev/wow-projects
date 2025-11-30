package wow.commons.model.spell.component;

import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.spell.Coefficient;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.spell.SpellTarget;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-11-30
 */
public sealed interface ComponentCommand {
	SpellTarget target();

	record DirectCommand(
			SpellTarget target,
			ComponentType type,
			Coefficient coefficient,
			int min,
			int max,
			DirectComponentBonus bonus,
			boolean bolt
	) implements ComponentCommand {
		public DirectCommand {
			Objects.requireNonNull(type);
			Objects.requireNonNull(coefficient);
		}

		public SpellSchool school() {
			return coefficient.school();
		}
	}
}
