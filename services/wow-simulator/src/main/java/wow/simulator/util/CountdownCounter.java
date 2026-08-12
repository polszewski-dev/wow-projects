package wow.simulator.util;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2026-08-12
 */
public class CountdownCounter {
	private int currentValue;
	private final Runnable actionOnZero;

	public CountdownCounter(int initialValue, Runnable actionOnZero) {
		if (initialValue <= 0) {
			throw new IllegalArgumentException("Only positive values");
		}
		Objects.requireNonNull(actionOnZero);
		this.currentValue = initialValue;
		this.actionOnZero = actionOnZero;
	}

	public void decrease() {
		if (currentValue <= 0) {
			throw new IllegalStateException("Counter already reached 0");
		}

		if (--currentValue == 0) {
			actionOnZero.run();
		}
	}
}
