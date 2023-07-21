package wow.commons.model.config;

import wow.commons.model.professions.ProfessionId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
public record ProfessionRestriction(ProfessionId professionId, int level) {
	public ProfessionRestriction {
		Objects.requireNonNull(professionId);
	}

	public static ProfessionRestriction of(ProfessionId professionId, Integer level) {
		if (professionId == null && level == null) {
			return null;
		}
		if (professionId != null && level != null) {
			return new ProfessionRestriction(professionId, level);
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", professionId, level);
	}
}
