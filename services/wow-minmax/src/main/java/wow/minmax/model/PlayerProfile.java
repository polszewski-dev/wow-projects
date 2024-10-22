package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.character.model.character.PlayerCharacter;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-11-04
 */
@Getter
@Setter
@AllArgsConstructor
public class PlayerProfile {
	private final UUID profileId;
	private final String profileName;
	private final CharacterClassId characterClassId;
	private final RaceId raceId;
	private LocalDateTime lastModified;
	private CharacterId lastModifiedCharacterId;

	public PlayerProfileInfo getProfileInfo() {
		return new PlayerProfileInfo(
				profileId,
				profileName,
				characterClassId,
				raceId,
				lastModified,
				lastModifiedCharacterId
		);
	}

	public CharacterId getCharacterId(PlayerCharacter character) {
		return new CharacterId(
				getProfileId(),
				character.getPhaseId(),
				character.getLevel(),
				character.getTarget().getCreatureType(),
				Character.getLevelDifference(character, character.getTarget())
		);
	}

	@Override
	public String toString() {
		return profileName;
	}
}
