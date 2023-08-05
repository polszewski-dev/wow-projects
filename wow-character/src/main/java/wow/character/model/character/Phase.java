package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionProficiencyId;
import wow.commons.model.professions.ProfessionSpecializationId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-03-30
 */
@AllArgsConstructor
@Getter
public class Phase implements Described {
	@NonNull
	private final PhaseId phaseId;

	@NonNull
	private final Description description;

	private final int maxLevel;

	private final ProfessionProficiencyId maxProficiencyId;

	@NonNull
	private final GameVersion gameVersion;

	@NonNull
	public GameVersionId getGameVersionId() {
		return gameVersion.getGameVersionId();
	}

	public CharacterProfession getCharacterProfession(ProfessionId professionId, ProfessionSpecializationId specializationId, int level) {
		Profession profession = gameVersion.getProfession(professionId);
		ProfessionSpecialization specialization = profession.getSpecialization(specializationId);
		return new CharacterProfession(profession, specialization, level);
	}

	public CharacterProfession getCharacterProfessionMaxLevel(ProfessionId professionId, ProfessionSpecializationId specializationId, int characterLevel) {
		Profession profession = gameVersion.getProfession(professionId);
		ProfessionSpecialization specialization = profession.getSpecialization(specializationId);
		int skillLevel = getMaxProfessionLevel(profession, characterLevel);
		return new CharacterProfession(profession, specialization, skillLevel);
	}

	private int getMaxProfessionLevel(Profession profession, int characterLevel) {
		return getAvailableProficiencies().stream()
				.filter(x -> characterLevel >= x.getReqLevel(profession.getType()))
				.map(ProfessionProficiency::getMaxSkilll)
				.max(Integer::compare)
				.orElseThrow();
	}

	private List<ProfessionProficiency> getAvailableProficiencies() {
		return gameVersion.getProficiencies().stream()
				.filter(x -> x.getProficiencyId().compareTo(maxProficiencyId) <= 0)
				.toList();
	}

	@Override
	public String toString() {
		return getName();
	}
}
