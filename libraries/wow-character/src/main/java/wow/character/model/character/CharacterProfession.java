package wow.character.model.character;

import lombok.Getter;
import wow.commons.model.profession.Profession;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecialization;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.Phase;

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

	@Override
	public String toString() {
		return "%s - %s".formatted(profession, specialization);
	}
}
