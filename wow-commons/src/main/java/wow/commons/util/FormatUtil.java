package wow.commons.util;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
public final class FormatUtil {
	public static String decimalPointOnlyIfNecessary(double value) {
		if (value % 1 <= PRECISION) {
			return Integer.toString((int)value);
		} else {
			return String.format("%.2f", value);
		}
	}

	public static String decimalPointOnlyIfNecessary(double value, String format) {
		return String.format(format, decimalPointOnlyIfNecessary(value));
	}

	private static final double PRECISION = 0.0001;

	private FormatUtil() {}
}
