package wow.commons.model.unit;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
@AllArgsConstructor
@Getter
public class CombatRatingInfo {
	private final int level;
	private final double spellCrit;
	private final double spellHit;
	private final double spellHaste;

	@Override
	public String toString() {
		return level + "";
	}
}
