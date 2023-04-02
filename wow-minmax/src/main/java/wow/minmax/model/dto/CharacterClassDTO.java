package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.character.CharacterClassId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterClassDTO {
	private CharacterClassId id;
	private String name;
	private String icon;
	private List<RaceDTO> races;
}
