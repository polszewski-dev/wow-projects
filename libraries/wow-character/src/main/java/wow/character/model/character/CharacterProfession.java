package wow.character.model.character;

import wow.commons.model.profession.Profession;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecialization;
import wow.commons.model.profession.ProfessionSpecializationId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public record CharacterProfession(Profession profession, ProfessionSpecialization specialization, int level) {
	public CharacterProfession {
		Objects.requireNonNull(profession);

		if (specialization != null && specialization.getProfessionId() != profession.getProfessionId()) {
			throw new IllegalArgumentException("Specialization doesn't match the profession");
		}
	}

	public ProfessionId professionId() {
		return profession.getProfessionId();
	}

	public ProfessionSpecializationId specializationId() {
		return specialization != null ? specialization.getSpecializationId() : null;
	}
}
