package wow.simulator.model.rng;

import wow.character.model.snapshot.RngStrategy;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public final class RngStrategies {
	public static final RngStrategy DOT = new RngStrategy() {
		@Override
		public double getHitChance(double hitChance) {
			return 1;
		}

		@Override
		public double getCritChance(double critChance) {
			return 0;
		}
	};

	public static RngStrategy directDamageStrategy(boolean critRoll) {
		return new RngStrategy() {
			@Override
			public double getHitChance(double hitChance) {
				return 1;
			}

			@Override
			public double getCritChance(double critChance) {
				return critRoll ? 1 : 0;
			}
		};
	}

	private RngStrategies() {}
}
