package wow.commons.model.unit;

import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
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

	public Profession getProfession() {
		return profession;
	}

	public int getLevel() {
		return level;
	}

	public ProfessionSpecialization getSpecialization() {
		return specialization;
	}
}
