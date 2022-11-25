package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
@AllArgsConstructor
@Getter
public class DirectDamageInfo {
	private final int minDmg;
	private final int maxDmg;
	private final int minDmg2;
	private final int maxDmg2;
}
