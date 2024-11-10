package wow.minmax.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.client.dto.CharacterClassDTO;
import wow.commons.client.dto.RaceDTO;

/**
 * User: POlszewski
 * Date: 2023-04-09
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlayerCharacterDTO {
	private String characterId;
	private CharacterClassDTO characterClass;
	private RaceDTO race;
}
