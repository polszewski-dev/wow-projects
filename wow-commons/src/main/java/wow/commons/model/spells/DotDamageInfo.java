package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
@AllArgsConstructor
@Getter
public class DotDamageInfo {
	private final int dotDmg;
	private final int numTicks;
	private final Duration tickInterval;
}
