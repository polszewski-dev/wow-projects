package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
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
	private final Map<CharacterId, Character> characterByKey;
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

	public Optional<Character> getCharacter(CharacterId key) {
		return Optional.ofNullable(characterByKey.get(key));
	}

	public void addCharacter(Character character) {
		CharacterId characterId = getCharacterId(character);

		if (characterByKey.containsKey(characterId)) {
			throw new IllegalArgumentException("character for key: %s already exists".formatted(characterId));
		}

		characterByKey.put(characterId, character);
	}

	public CharacterId getCharacterId(Character character) {
		return new CharacterId(
				getProfileId(),
				character.getPhaseId(),
				character.getLevel(),
				character.getTargetEnemy().getEnemyType(),
				character.getTargetEnemy().getLevelDifference()
		);
	}

	@Override
	public String toString() {
		return profileName;
	}
}
