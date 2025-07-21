package wow.character.model.snapshot;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
@Setter
public class EffectDurationSnapshot {
	private AnyDuration duration;
	private int numTicks;
	private Duration tickInterval;

	public double getDurationSeconds() {
		return ((Duration) duration).getSeconds();
	}
}
