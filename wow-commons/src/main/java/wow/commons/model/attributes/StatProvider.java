package wow.commons.model.attributes;

import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public interface StatProvider {
	double hitChance();

	double critChance();

	Duration castTime();
}
