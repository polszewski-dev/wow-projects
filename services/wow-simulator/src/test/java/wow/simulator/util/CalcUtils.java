package wow.simulator.util;

/**
 * User: POlszewski
 * Date: 2025-02-18
 */
public final class CalcUtils {
	public static int increaseByPct(int originalValue, int pct) {
		return (int) (originalValue * (100 + pct) / 100.0);
	}

	public static double increaseByPct(double originalValue, int pct) {
		return originalValue * (100 + pct) / 100.0;
	}

	public static int getPercentOf(int pct, int value) {
		return (value * pct) / 100;
	}

	public static double getPercentOf(double pct, double value) {
		return (value * pct) / 100;
	}

	private CalcUtils() {}
}
