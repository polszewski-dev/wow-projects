package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
@AllArgsConstructor
@Getter
public class BaseStatInfo implements TimeRestricted {
	private final int level;
	private final CharacterClass characterClass;
	private final Race race;
	private final TimeRestriction timeRestriction;
	private final int baseStrength;
	private final int baseAgility;
	private final int baseStamina;
	private final int baseIntellect;
	private final int baseSpirit;
	private final int baseHP;
	private final int baseMana;
	private final Percent baseSpellCritPct;
	private final double intellectPerCritPct;

	@Override
	public String toString() {
		return String.format("%s,%s,%s", characterClass, race, level);
	}
}