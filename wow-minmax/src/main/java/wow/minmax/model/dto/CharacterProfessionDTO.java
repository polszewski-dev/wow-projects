package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;

/**
 * User: POlszewski
 * Date: 2023-01-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterProfessionDTO {
	private Profession profession;
	private ProfessionSpecialization specialization;
}
