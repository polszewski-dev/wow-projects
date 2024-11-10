package wow.commons.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;

/**
 * User: POlszewski
 * Date: 2023-01-07
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CharacterProfessionDTO {
	private ProfessionId profession;
	private ProfessionSpecializationId specialization;
	private int level;
}
