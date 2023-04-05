package wow.commons.model.attributes;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public interface StatProvider {
	double getHitChance();

	double getCritChance();

	double getEffectiveCastTime();

	static StatProvider dummyValues() {
		return StatProvider.fixedValues(0.99, 0.30, 2.5);
	}

	static StatProvider fixedValues(double hitChance, double critChance, double effectiveCastTime) {
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
			public double getEffectiveCastTime() {
				return effectiveCastTime;
			}
		};
	}
}
