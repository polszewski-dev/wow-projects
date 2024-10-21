package wow.commons.constant;

import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2021-10-20
 */
public final class SpellConstants {
	public static final Duration GCD = Duration.seconds(1.5);
	public static final Duration MIN_GCD = Duration.seconds(1);

	public static final int HEALTH_PER_STAMINA = 10;
	public static final int MANA_PER_INTELLECT = 15;

	public static final int UNLIMITED_RANGE = 50_000;

	private SpellConstants() {}
}
