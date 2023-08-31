package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;

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
	private int level;
}
