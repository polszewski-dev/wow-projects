package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.Race;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
@AllArgsConstructor
@Getter
public class BaseStatInfo {
	private final int level;
	private final CharacterClass characterClass;
	private final Race race;
	private final int baseStrength;
	private final int baseAgility;
	private final int baseStamina;
	private final int baseIntellect;
	private final int baseSpirit;
	private final int baseHealth;
	private final int baseMana;
	private final Percent baseSpellCritPct;
	private final double intellectPerCritPct;

	@NonNull
	private final GameVersion gameVersion;

	@NonNull
	public RaceId getRaceId() {
		return race.getRaceId();
	}

	@NonNull
	public CharacterClassId getCharacterClassId() {
		return characterClass.getCharacterClassId();
	}

	public GameVersionId getGameVersionId() {
		return gameVersion.getGameVersionId();
	}

	@Override
	public String toString() {
		return String.format("%s,%s,%s", getCharacterClassId(), getRaceId(), level);
	}
}
