package wow.character.model.character;

import wow.commons.model.profession.Profession;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecialization;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.Phase;

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

	public static CharacterProfession getCharacterProfession(Phase phase, ProfessionId professionId, ProfessionSpecializationId specializationId, int level) {
		var profession = phase.getGameVersion().getProfession(professionId).orElseThrow();
		var specialization = specializationId == null ? null : profession.getSpecialization(specializationId).orElseThrow();

		return new CharacterProfession(profession, specialization, level);
	}

	public static CharacterProfession getCharacterProfessionMaxLevel(Phase phase, ProfessionId professionId, ProfessionSpecializationId specializationId, int characterLevel) {
		var profession = phase.getGameVersion().getProfession(professionId).orElseThrow();
		var specialization = specializationId == null ? null : profession.getSpecialization(specializationId).orElseThrow();
		var skillLevel = phase.getMaxProfessionLevel(profession, characterLevel);

		return new CharacterProfession(profession, specialization, skillLevel);
	}
}
