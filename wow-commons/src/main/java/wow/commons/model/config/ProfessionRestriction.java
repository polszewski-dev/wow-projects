package wow.commons.model.config;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.professions.ProfessionId;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ProfessionRestriction {
	@NonNull
	private final ProfessionId professionId;
	private final int level;

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
