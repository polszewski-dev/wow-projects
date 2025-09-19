package wow.commons.model;

import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2019-08-14
 */
public record Duration(long millis) implements AnyDuration {
	public static final Duration ZERO = new Duration(0);
	public static final InfiniteDuration INFINITE = new InfiniteDuration();

	public Duration {
		if (millis < 0) {
			throw new IllegalArgumentException();
		}
	}

	public static Duration parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		if (value.matches("\\d+\\.?\\d*")) {
			return seconds(Double.parseDouble(value));
		}

		var matcher = PATTERN.matcher(value);

		if (!matcher.find()) {
			throw new IllegalArgumentException(value);
		}

		var hourStr = matcher.group(2);
		var minStr = matcher.group(4);
		var secStr = matcher.group(6);
		var millisStr = matcher.group(8);

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

	private static final Pattern PATTERN = Pattern.compile("^-?((\\d+)h)?((\\d+)m)?((\\d+)s)?((\\d+)ms)?$");

	public static Duration seconds(long seconds) {
		return millis(seconds * 1000);
	}

	public static Duration seconds(double seconds) {
		return millis(Math.round(seconds * 1000));
	}

	public static Duration minutes(long minutes) {
		return seconds(minutes * 60);
	}

	public static Duration hours(long hours) {
		return seconds(hours * 60 * 60);
	}

	public static Duration millis(long millis) {
		if (millis == 0) {
			return ZERO;
		}
		return new Duration(millis);
	}

	public double getSeconds() {
		return millis / 1000.0;
	}

	@Override
	public boolean isZero() {
		return millis == 0;
	}

	@Override
	public boolean isPositive() {
		return millis > 0;
	}

	@Override
	public boolean isInfinite() {
		return false;
	}

	public Duration add(Duration duration) {
		return millis(this.millis + duration.millis);
	}

	public AnyDuration add(AnyDuration anyDuration) {
		if (anyDuration instanceof Duration duration) {
			return add(duration);
		}
		return anyDuration;
	}

	public double divideBy(Duration duration) {
		if (duration.isZero()) {
			throw new IllegalArgumentException("X / 0");
		}
		return (double) this.millis / duration.millis;
	}

	public Duration min(Duration duration) {
		return this.millis < duration.millis ? this : duration;
	}

	public Duration max(Duration duration) {
		return this.millis > duration.millis ? this : duration;
	}

	@Override
	public int compareTo(AnyDuration anyDuration) {
		if (anyDuration instanceof Duration duration) {
			return Long.compare(this.millis, duration.millis);
		}
		return -1;
	}

	@Override
	public String toString() {
		if (millis == 0) {
			return "0";
		}

		long x = Math.abs(millis);
		long milliseconds = x % 1000;
		x /= 1000;
		long seconds = x % 60;
		x /= 60;
		long minutes = x % 60;
		x /= 60;
		long hours = x;

		return (hours != 0 ? hours + "h" : "") +
				(minutes != 0 ? minutes + "m" : "") +
				(seconds != 0 ? seconds + "s" : "") +
				(milliseconds != 0 ? milliseconds + "ms" : "");
	}
}
