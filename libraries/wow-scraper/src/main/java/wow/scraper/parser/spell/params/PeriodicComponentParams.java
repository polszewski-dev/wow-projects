package wow.scraper.parser.spell.params;

import wow.commons.model.effect.component.ComponentType;
import wow.commons.model.spell.Coefficient;
import wow.commons.model.spell.SpellTarget;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-09
 */
public record PeriodicComponentParams(
		SpellTarget target,
		ComponentType type,
		Coefficient coefficient,
		String totalAmount,
		String tickAmount,
		String tickWeights
) {
	public PeriodicComponentParams {
		Objects.requireNonNull(target);
		Objects.requireNonNull(type);

		if (totalAmount != null && tickAmount != null) {
			throw new IllegalArgumentException("Both total and tick amounts can't be provided");
		}

		if (totalAmount == null && tickAmount == null) {
			throw new IllegalArgumentException("Either total or tick amount must be provided");
		}
	}
}
