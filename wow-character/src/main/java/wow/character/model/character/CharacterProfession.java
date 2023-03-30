package wow.character.model.character;

import lombok.Getter;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public class CharacterProfession {
	private final ProfessionId professionId;
	private final ProfessionSpecializationId specializationId;

	public CharacterProfession(ProfessionId professionId, ProfessionSpecializationId specializationId) {
		this.professionId = professionId;
		this.specializationId = specializationId;
		if (specializationId != null && specializationId.getProfessionId() != professionId) {
			throw new IllegalArgumentException("Specialization doesn't match the profession");
		}
	}
}
