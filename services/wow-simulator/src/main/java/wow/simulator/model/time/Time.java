package wow.simulator.model.time;

import wow.commons.model.Duration;
import wow.commons.util.FormatUtil;

import java.text.DecimalFormat;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public record Time(long timestamp) implements Comparable<Time> {
	private static final int INF_TIMESTAMP = Integer.MAX_VALUE;

	public static final Time ZERO = new Time(0);
	public static final Time INFINITY = new Time(INF_TIMESTAMP);

	public Time {
		if (timestamp < 0) {
			throw new IllegalArgumentException("Negative timestamp: " + timestamp);
		}
		timestamp = Math.min(timestamp, INF_TIMESTAMP);
	}

	public static Time at(double secondsFromZero) {
		return new Time((int)(secondsFromZero * 1000));
	}

	public double secondsSinceZero() {
		return timestamp / 1000.0;
	}

	private double asDecimal() {
		return timestamp / 1000.0;
	}

	public boolean isAtZero() {
		return timestamp == 0;
	}

	public boolean isInInfinity() {
		return timestamp == INF_TIMESTAMP;
	}

	public Time add(Duration duration) {
		if (this.isInInfinity() || duration.isInfinite()) {
			return INFINITY;
		}
		return new Time(this.timestamp + duration.millis());
	}

	public Time subtract(Duration duration) {
		if (duration.isInfinite()) {
			throw new IllegalArgumentException("Can't subtract INF");
		}
		if (this.isInInfinity()) {
			return this;
		}
		return new Time(this.timestamp - duration.millis());
	}

	public Duration subtract(Time time) {
		if (this.isInInfinity()) {
			if (time.isInInfinity()) {
				throw new IllegalArgumentException("Can't subtract infinities");
			}
			return Duration.INFINITE;
		}
		if (time.isInInfinity()) {
			throw new IllegalArgumentException("Can't subtract infinity");
		}
		return Duration.millis(this.timestamp - time.timestamp);
	}

	public Time min(Time time) {
		return this.timestamp < time.timestamp ? this : time;
	}

	public Time max(Time time) {
		return this.timestamp > time.timestamp ? this : time;
	}

	public boolean after(Time time) {
		return this.compareTo(time) > 0;
	}

	public boolean before(Time time) {
		return this.compareTo(time) < 0;
	}

	@Override
	public int compareTo(Time time) {
		return Long.compare(this.timestamp, time.timestamp);
	}

	@Override
	public String toString() {
		if (isInInfinity()) {
			return "INF";
		}
		return new DecimalFormat("0.000", FormatUtil.DECIMAL_FORMAT_SYMBOLS).format(asDecimal());
	}
}
