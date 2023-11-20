package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2022-12-25
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlayerProfileInfoDTO {
	private UUID profileId;
	private String profileName;
	private CharacterClassDTO characterClass;
	private RaceDTO race;
	private LocalDateTime lastModified;
	private String lastUsedCharacterId;
}
