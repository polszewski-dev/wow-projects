package wow.simulator.model.time;

import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public final class Clock {
	private Time now = Time.ZERO;

	public Time now() {
		return now;
	}

	public void advanceTo(Time time) {
		if (timeInThePast(time)) {
			throw new IllegalArgumentException("Advancing from %s to %s moves clock to the past!".formatted(now, time));
		}
		now = time;
	}

	public boolean timeInTheFuture(AnyTime time) {
		return time.compareTo(now) > 0;
	}

	public boolean timeInTheFutureOrPresent(AnyTime time) {
		return time.compareTo(now) >= 0;
	}

	public boolean timeInThePast(AnyTime time) {
		return time.compareTo(now) < 0;
	}

	public boolean timeInThePastOrPresent(AnyTime time) {
		return time.compareTo(now) <= 0;
	}

	public boolean timeInThePresent(AnyTime time) {
		return time.compareTo(now) == 0;
	}

	public Time after(Duration duration) {
		return now.add(duration);
	}

	public AnyTime after(AnyDuration duration) {
		return now.add(duration);
	}

	public Time afterSeconds(int seconds) {
		return after(Duration.seconds(seconds));
	}
}
