package wow.scraper.model;

import wow.commons.model.effect.Effect;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-04-09
 */
public record RandomEnchant(
		String suffix,
		List<Effect> effects
) {
	public RandomEnchant {
		Objects.requireNonNull(suffix);
		Objects.requireNonNull(effects);
	}
}
