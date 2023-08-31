package wow.commons.model.spell;

import wow.commons.model.Percent;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record DotDamageInfo(Percent coeffDot, int dotDmg, TickScheme tickScheme) {
	public DotDamageInfo {
		Objects.requireNonNull(coeffDot);
	}
}
