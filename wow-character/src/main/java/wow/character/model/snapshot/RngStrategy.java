package wow.character.model.snapshot;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public interface RngStrategy {
	double getHitChance(double hitChance);

	double getCritChance(double critChance);

	default double getDamage(double min, double max) {
		return (min + max) / 2;
	}

	RngStrategy AVERAGED = new RngStrategy() {
		@Override
		public double getHitChance(double hitChance) {
			return hitChance;
		}

		@Override
		public double getCritChance(double critChance) {
			return critChance;
		}
	};
}
