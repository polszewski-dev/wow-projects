package wow.commons.model.spell.component;

import wow.commons.model.spell.AbilityId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-30
 */
public record DirectComponentBonus(
		int min,
		int max,
		AbilityId requiredEffect
) {
	public DirectComponentBonus {
		Objects.requireNonNull(requiredEffect);
	}
}
