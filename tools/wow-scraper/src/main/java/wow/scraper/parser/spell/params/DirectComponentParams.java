package wow.scraper.parser.spell.params;

import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.spell.Coefficient;
import wow.commons.model.spell.SpellTarget;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-09
 */
public record DirectComponentParams(
		SpellTarget target,
		ComponentType type,
		Coefficient coefficient,
		String min,
		String max,
		DirectComponentBonusParams bonus,
		boolean bolt
) {
	public DirectComponentParams {
		Objects.requireNonNull(target);
		Objects.requireNonNull(type);
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);
	}
}
