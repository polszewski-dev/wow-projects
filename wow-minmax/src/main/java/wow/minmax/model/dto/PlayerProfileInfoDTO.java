package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2022-12-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfileInfoDTO {
	private UUID profileId;
	private String profileName;
	private CharacterClassDTO characterClass;
	private RaceDTO race;
	private LocalDateTime lastModified;
	private String lastUsedCharacterId;
}
