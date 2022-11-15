package wow.minmax.model.persistent;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;

import java.io.Serializable;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
@Data
@AllArgsConstructor
public class CharacterProfessionPO implements Serializable {
	private Profession profession;
	private int level;
	private ProfessionSpecialization specialization;
}
