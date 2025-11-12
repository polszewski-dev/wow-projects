package wow.character.model.snapshot;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public interface RngStrategy {
	default double getAmount(double min, double max) {
		return (min + max) / 2;
	}

	RngStrategy AVERAGED = new RngStrategy() {};
}
