package wow.commons.model.spells;

import wow.commons.model.Percent;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record DirectDamageInfo(Percent coeffDirect, int minDmg, int maxDmg, int minDmg2, int maxDmg2) {
	public DirectDamageInfo {
		Objects.requireNonNull(coeffDirect);
	}
}
