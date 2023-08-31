package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: POlszewski
 * Date: 2023-04-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCharacterDTO {
	private String characterId;
	private CharacterClassDTO characterClass;
	private RaceDTO race;
}
