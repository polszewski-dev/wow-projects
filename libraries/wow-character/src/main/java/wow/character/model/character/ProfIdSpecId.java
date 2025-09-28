package wow.character.model.character;

import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-09-28
 */
public record ProfIdSpecId(ProfessionId professionId, ProfessionSpecializationId specializationId) {
	public ProfIdSpecId {
		Objects.requireNonNull(professionId);
	}

	public ProfIdSpecId(ProfessionId professionId) {
		this(professionId, null);
	}
}
