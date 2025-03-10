package wow.commons.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
public final class FormatUtil {
	public static String decimalPointOnlyIfNecessary(double value) {
		return decimalPointOnlyIfNecessary(value, "0.00");
	}

	public static String decimalPointOnlyIfNecessary(double value, String format) {
		if (Math.abs(value % 1) <= PRECISION) {
			return Integer.toString((int)value);
		} else {
			return new DecimalFormat(format, DECIMAL_FORMAT_SYMBOLS).format(value);
		}
	}

	public static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = DecimalFormatSymbols.getInstance();

	static {
		DECIMAL_FORMAT_SYMBOLS.setDecimalSeparator('.');
	}

	private static final double PRECISION = 0.0001;

	private FormatUtil() {}
}
