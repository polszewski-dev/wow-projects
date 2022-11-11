package wow.commons.model.attributes;

import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public interface StatProvider {
	double getHitChance();

	double getCritChance();

	Duration getEffectiveCastTime();

	static StatProvider fixedValues(double hitChance, double critChance, Duration effectiveCastTime) {
		return new StatProvider() {
			@Override
			public double getHitChance() {
				return hitChance;
			}

			@Override
			public double getCritChance() {
				return critChance;
			}

			@Override
			public Duration getEffectiveCastTime() {
				return effectiveCastTime;
			}
		};
	}
}
