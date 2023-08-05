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
	private final Profession profession;
	private final ProfessionSpecialization specialization;
	private final int level;

	public CharacterProfession(Profession profession, ProfessionSpecialization specialization, int level) {
		if (specialization != null && specialization.getProfessionId() != profession.getProfessionId()) {
			throw new IllegalArgumentException("Specialization doesn't match the profession");
		}
		this.profession = profession;
		this.specialization = specialization;
		this.level = level;
	}

	public ProfessionId getProfessionId() {
		return profession.getProfessionId();
	}

	public ProfessionSpecializationId getSpecializationId() {
		return specialization != null ? specialization.getSpecializationId() : null;
	}

	@Override
	public String toString() {
		return "%s - %s".formatted(profession, specialization);
	}
}
