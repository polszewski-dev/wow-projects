package wow.character.model.snapshot;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
@Setter
public class EffectDurationSnapshot {
	private Duration duration;
	private int numTicks;
	private Duration tickInterval;
}
