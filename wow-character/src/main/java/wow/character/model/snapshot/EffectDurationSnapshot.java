package wow.character.model.snapshot;

import lombok.Getter;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
@Setter
public class EffectDurationSnapshot {
	private double duration;
	private double tickInterval;
}
