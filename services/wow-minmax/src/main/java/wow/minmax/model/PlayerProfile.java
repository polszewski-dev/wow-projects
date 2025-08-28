package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-11-04
 */
@Document("profile")
@AllArgsConstructor
@Getter
@Setter
public class PlayerProfile {
	@Id
	private String profileId;
	private String profileName;
	private CharacterClassId characterClassId;
	private RaceId raceId;
	private LocalDateTime lastModified;
	private String lastModifiedCharacterId;

	public UUID getProfileIdAsUUID() {
		return UUID.fromString(profileId);
	}

	public CharacterId getLastModifiedCharacterIdAsCharacterId() {
		return CharacterId.parse(lastModifiedCharacterId);
	}

	public PlayerProfileInfo getProfileInfo() {
		return new PlayerProfileInfo(
				getProfileIdAsUUID(),
				profileName,
				characterClassId,
				raceId,
				lastModified,
				getLastModifiedCharacterIdAsCharacterId()
		);
	}

	@Override
	public String toString() {
		return profileName;
	}
}
