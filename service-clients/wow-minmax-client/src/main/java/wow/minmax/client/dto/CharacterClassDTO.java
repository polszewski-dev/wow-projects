package wow.minmax.client.dto;

import wow.commons.model.character.CharacterClassId;
import wow.commons.model.spell.SpellSchool;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
public record CharacterClassDTO(
		CharacterClassId id,
		String name,
		String icon,
		List<RaceDTO> races,
		List<SpellSchool> spellSchools
) {
}
