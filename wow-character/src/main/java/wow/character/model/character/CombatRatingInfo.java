package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.pve.GameVersion;

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

	@NonNull
	private final GameVersion gameVersion;

	@Override
	public String toString() {
		return level + "";
	}
}
