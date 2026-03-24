package wow.estimator.model;

import wow.commons.model.buff.BuffCategory;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.SpellSchool;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2026-03-24
 */
public record EffectInstance(
		Effect effect,
		int numStacks
) {
	public EffectInstance {
		Objects.requireNonNull(effect);
	}

	public boolean isSchoolPrevented(SpellSchool school) {
		return effect.isSchoolPrevented(school);
	}

	@Override
	public String toString() {
		return "%s x%s".formatted(effect, numStacks);
	}

	public boolean hasCategory(BuffCategory buffCategory) {
		return false;
	}
}
