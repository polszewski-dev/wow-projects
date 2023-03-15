package wow.commons.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2019-08-14
 */
public final class Duration implements Comparable<Duration> {
	private static final Pattern PATTERN = Pattern.compile("^-?((\\d+)h)?((\\d+)m)?((\\d+)s)?((\\d+)ms)?$");
	private static final int INF_MILLIS = Integer.MAX_VALUE;
	private static final int NEG_INF_MILLIS = Integer.MIN_VALUE;

	public static final Duration ZERO = new Duration(0);
	public static final Duration INFINITE = new Duration(INF_MILLIS);
	public static final Duration NEG_INFINITE = new Duration(NEG_INF_MILLIS);

	private final long millis;

	private Duration(long millis) {
		this.millis = Math.max(Math.min(millis, INF_MILLIS), NEG_INF_MILLIS);
	}

	public static Duration seconds(double seconds) {
		return millis((long)(seconds * 1000));
	}

	public static Duration seconds(Double seconds) {
		return seconds != null ? seconds((double)seconds) : null;
	}

	public static Duration seconds(Integer seconds) {
		return seconds != null ? seconds((double)seconds) : null;
	}

	public static Duration millis(long millis) {
		if (millis == 0) {
			return ZERO;
		}
		if (millis == INF_MILLIS) {
			return INFINITE;
		}
		if (millis == NEG_INF_MILLIS) {
			return NEG_INFINITE;
		}
		return new Duration(millis);
	}

	public static Duration parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		if (value.matches("-?\\d+\\.?\\d*")) {
			return Duration.seconds(Double.parseDouble(value));
		}

		Matcher matcher = PATTERN.matcher(value);

		if (!matcher.find()) {
			throw new IllegalArgumentException(value);
		}

		String hourStr = matcher.group(2);
		String minStr = matcher.group(4);
		String secStr = matcher.group(6);
		String millisStr = matcher.group(8);

		long millis = 0;

		if (hourStr != null) {
			millis += Long.parseLong(hourStr) * 60 * 60 * 1000;
		}

		if (minStr != null) {
			millis += Long.parseLong(minStr) * 60 * 1000;
		}

		if (secStr != null) {
			millis += Long.parseLong(secStr) * 1000;
		}

		if (millisStr != null) {
			millis += Long.parseLong(millisStr);
		}

		if (value.startsWith("-")) {
			millis = -millis;
		}

		return millis(millis);
	}

	public long getMillis() {
		return millis;
	}

	public double getSeconds() {
		if (this.isInfinite() || this.isNegInfinite()) {
			throw new IllegalArgumentException("Too many seconds in INF");
		}
		return millis / 1000.0;
	}

	public boolean isZero() {
		return millis == 0;
	}

	public boolean isPositive() {
		return millis > 0;
	}

	public boolean isNegative() {
		return millis < 0;
	}

	public boolean isInfinite() {
		return millis == INF_MILLIS;
	}

	public boolean isNegInfinite() {
		return millis == NEG_INF_MILLIS;
	}

	public Duration negate() {
		if (this.isInfinite()) {
			return NEG_INFINITE;
		}
		if (this.isNegInfinite()) {
			return INFINITE;
		}
		return millis(-millis);
	}

	public Duration add(Duration duration) {
		if (this.isInfinite() && duration.isNegInfinite()) {
			throw new IllegalArgumentException("INF + -INF");
		}
		if (this.isNegInfinite() && duration.isInfinite()) {
			throw new IllegalArgumentException("-INF + INF");
		}
		if (this.isInfinite() || this.isNegInfinite()) {
			return this;
		}
		if (duration.isInfinite() || duration.isNegInfinite()) {
			return duration;
		}
		return millis(this.millis + duration.millis);
	}

	public Duration subtract(Duration duration) {
		if (this.isInfinite() && duration.isInfinite()) {
			throw new IllegalArgumentException("INF - INF");
		}
		if (this.isNegInfinite() && duration.isNegInfinite()) {
			throw new IllegalArgumentException("-INF - (-INF)");
		}
		if (this.isInfinite() || this.isNegInfinite()) {
			return this;
		}
		if (duration.isInfinite() || duration.isNegInfinite()) {
			return duration.negate();
		}
		return millis(this.millis - duration.millis);
	}

	public Duration multiplyBy(double factor) {
		if (factor == 0) {
			return ZERO;
		}
		if (this.isInfinite() || this.isNegInfinite()) {
			return factor > 0 ? this : this.negate();
		}
		return millis((long)(factor * millis));
	}

	public Duration divideBy(double factor) {
		if (factor == 0) {
			if (this.isZero()) {
				throw new IllegalArgumentException("0 / 0");
			}
			return this.isPositive() ? INFINITE : NEG_INFINITE;
		}
		if (this.isInfinite() || this.isNegInfinite()) {
			return factor > 0 ? this : this.negate();
		}
		return millis((int)(millis / factor));
	}

	public double divideBy(Duration duration) {
		if (this.isInfinite() || this.isNegInfinite()) {
			throw new IllegalArgumentException("Can't divide INF");
		}
		if (duration.isInfinite() || duration.isNegInfinite()) {
			return 0;
		}
		if (duration.isZero()) {
			throw new IllegalArgumentException("X / 0");
		}
		return (double)this.millis / duration.millis;
	}

	public Duration min(Duration duration) {
		return this.millis < duration.millis ? this : duration;
	}

	public Duration max(Duration duration) {
		return this.millis > duration.millis ? this : duration;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Duration duration = (Duration) o;
		return millis == duration.millis;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(millis);
	}

	@Override
	public int compareTo(Duration duration) {
		return Long.compare(this.millis, duration.millis);
	}

	@Override
	public String toString() {
		if (this.isZero()) {
			return "0";
		}

		if (this.isInfinite()) {
			return "INFINITE";
		}

		if (this.isNegInfinite()) {
			return "-INFINITE";
		}

		long x = Math.abs(millis);
		long milliseconds = x % 1000;
		x /= 1000;
		long seconds = x % 60;
		x /= 60;
		long minutes = x % 60;
		x /= 60;
		long hours = x;

		return
				(millis < 0 ? "-" : "") +
				(hours != 0 ? hours + "h" : "") +
				(minutes != 0 ? minutes + "m" : "") +
				(seconds != 0 ? seconds + "s" : "") +
				(milliseconds != 0 ? milliseconds + "ms" : "");
	}
}
