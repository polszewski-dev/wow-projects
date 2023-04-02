package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-03-30
 */
@AllArgsConstructor
@Getter
public class Profession implements Described {
	@NonNull
	private final ProfessionId professionId;

	@NonNull
	private final Description description;

	private final List<ProfessionSpecialization> specializations = new ArrayList<>();

	@NonNull
	private final GameVersion gameVersion;

	public ProfessionSpecialization getSpecialization(ProfessionSpecializationId specializationId) {
		if (specializationId == null) {
			return null;
		}
		return specializations.stream()
				.filter(x -> x.getSpecializationId() == specializationId)
				.findFirst()
				.orElseThrow();
	}

	@Override
	public String toString() {
		return getName();
	}
}
