package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;

/**
 * User: POlszewski
 * Date: 2023-01-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterProfessionDTO {
	private ProfessionId profession;
	private ProfessionSpecializationId specialization;
}
