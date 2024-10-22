package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User: POlszewski
 * Date: 2021-12-17
 */
@Document("profile")
@AllArgsConstructor
@Getter
@Setter
public class PlayerProfilePO implements Serializable {
	@Id
	private String profileId;
	private String profileName;
	private CharacterClassId characterClassId;
	private RaceId raceId;
	private LocalDateTime lastModified;
	private String lastModifiedCharacterId;
}
