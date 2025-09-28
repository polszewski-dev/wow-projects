package wow.character.model.character;

import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-09-28
 */
public record ProfIdSpecIdLevel(ProfessionId professionId, ProfessionSpecializationId specializationId, int level) {
	public ProfIdSpecIdLevel {
		Objects.requireNonNull(professionId);
	}

	public ProfIdSpecIdLevel(ProfessionId professionId, int level) {
		this(professionId, null, level);
	}

	public ProfIdSpecIdLevel(CharacterProfession characterProfession) {
		this(characterProfession.professionId(), characterProfession.specializationId(), characterProfession.level());
	}
}
