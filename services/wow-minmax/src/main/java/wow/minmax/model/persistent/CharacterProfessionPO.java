package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@AllArgsConstructor
@Getter
@Setter
public class CharacterProfessionPO implements Serializable {
	private ProfessionId professionId;
	private ProfessionSpecializationId specializationId;
	private int level;
}
