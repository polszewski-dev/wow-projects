package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;

/**
 * User: POlszewski
 * Date: 2023-03-30
 */
@AllArgsConstructor
@Getter
public class ProfessionSpecialization implements Described {
	@NonNull
	private final ProfessionSpecializationId specializationId;

	@NonNull
	private final Description description;

	@NonNull
	private final Profession profession;

	public ProfessionId getProfessionId() {
		return profession.getProfessionId();
	}

	@Override
	public String toString() {
		return getName();
	}
}
