package wow.commons.model.unit;

import lombok.Getter;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public class CharacterProfession {
	private final Profession profession;
	private final int level;
	private final ProfessionSpecialization specialization;

	public CharacterProfession(Profession profession, int level, ProfessionSpecialization specialization) {
		this.profession = profession;
		this.level = level;
		this.specialization = specialization;
		if (specialization != null && specialization.getProfession() != profession) {
			throw new IllegalArgumentException("Specialization doesn't match the profession");
		}
	}
}
