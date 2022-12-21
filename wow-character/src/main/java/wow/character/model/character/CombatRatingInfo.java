package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
@AllArgsConstructor
@Getter
public class CombatRatingInfo implements TimeRestricted {
	private final int level;
	private final TimeRestriction timeRestriction;
	private final double spellCrit;
	private final double spellHit;
	private final double spellHaste;

	@Override
	public String toString() {
		return level + "";
	}
}
