package wow.commons.util;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
public final class FormatUtil {
	public static String decimalPointOnlyIfNecessary(double value) {
		if (value % 1 == 0) {
			return Integer.toString((int)value);
		} else {
			return String.format("%.2f", value);
		}
	}

	public static String decimalPointOnlyIfNecessary(double value, String format) {
		return String.format(format, decimalPointOnlyIfNecessary(value));
	}
	private FormatUtil() {}
}
