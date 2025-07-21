package wow.simulator.model.time;

import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.util.FormatUtil;

import java.text.DecimalFormat;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public record Time(long timestamp) implements AnyTime {
	public static final Time ZERO = new Time(0);

	public Time {
		if (timestamp < 0) {
			throw new IllegalArgumentException("Negative timestamp: " + timestamp);
		}
	}

	public static Time at(double secondsFromZero) {
		return new Time((int)(secondsFromZero * 1000));
	}

	public double secondsSinceZero() {
		return timestamp / 1000.0;
	}

	public boolean isInInfinity() {
		return false;
	}

	public Time add(Duration duration) {
		return new Time(this.timestamp + duration.millis());
	}

	@Override
	public AnyTime add(AnyDuration anyDuration) {
		if (anyDuration instanceof Duration duration) {
			return this.add(duration);
		}
		return TIME_IN_INFINITY;
	}

	public Duration subtract(Time time) {
		return Duration.millis(this.timestamp - time.timestamp);
	}

	@Override
	public int compareTo(AnyTime anyTime) {
		if (anyTime instanceof Time time) {
			return Long.compare(this.timestamp, time.timestamp);
		}
		return -1;
	}

	@Override
	public String toString() {
		return new DecimalFormat("0.000", FormatUtil.DECIMAL_FORMAT_SYMBOLS).format(secondsSinceZero());
	}
}
