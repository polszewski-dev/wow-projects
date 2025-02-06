package wow.simulator.util;

/**
 * User: POlszewski
 * Date: 2025-02-06
 */
public class RoundingReminder {
	private double reminder;

	public int roundValue(double value) {
		var roundedValue = (int) (value + reminder);

		reminder = value + reminder - roundedValue;

		var roundingPrecision = 0.001;

		if (1 - reminder < roundingPrecision) {
			++roundedValue;
			reminder = 0;
		}

		return roundedValue;
	}
}
