package wow.commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.spell.SpellSchool;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CharacterClassDTO {
	private CharacterClassId id;
	private String name;
	private String icon;
	private List<RaceDTO> races;
	private List<SpellSchool> spellSchools;
}
