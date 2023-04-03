package wow.commons.constants;

import wow.commons.model.Duration;
import wow.commons.model.Percent;

/**
 * User: POlszewski
 * Date: 2021-10-20
 */
public final class SpellConstants {
	public static final Duration GCD = Duration.seconds(1.5);
	public static final Duration MIN_GCD = Duration.seconds(1);

	public static final Percent BASE_CRIT_DAMAGE_BONUS_PCT = Percent.of(50);

	public static final Duration FLIGHT_TIME = Duration.seconds(0.5);

	private SpellConstants() {}
}
