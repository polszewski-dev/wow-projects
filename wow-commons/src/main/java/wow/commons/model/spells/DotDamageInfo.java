package wow.commons.model.spells;

import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record DotDamageInfo(int dotDmg, int numTicks, Duration tickInterval) {
}
