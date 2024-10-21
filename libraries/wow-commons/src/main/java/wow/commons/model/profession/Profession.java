package wow.commons.model.profession;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.pve.GameVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	@NonNull
	private final ProfessionType type;

	private final List<ProfessionSpecialization> specializations = new ArrayList<>();

	@NonNull
	private final GameVersion gameVersion;

	public Optional<ProfessionSpecialization> getSpecialization(ProfessionSpecializationId specializationId) {
		return specializations.stream()
				.filter(x -> x.getSpecializationId() == specializationId)
				.findFirst();
	}

	@Override
	public String toString() {
		return getName();
	}
}
