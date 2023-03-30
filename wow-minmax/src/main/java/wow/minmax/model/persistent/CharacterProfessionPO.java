package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@Data
@AllArgsConstructor
public class CharacterProfessionPO implements Serializable {
	private ProfessionId professionId;
	private ProfessionSpecializationId specializationId;
}
