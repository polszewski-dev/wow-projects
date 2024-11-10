package wow.commons.client.dto;

import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;

/**
 * User: POlszewski
 * Date: 2023-01-07
 */
public record CharacterProfessionDTO(
		ProfessionId profession,
		ProfessionSpecializationId specialization,
		int level
) {
}
