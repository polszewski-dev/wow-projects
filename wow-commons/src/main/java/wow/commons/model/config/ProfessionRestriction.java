package wow.commons.model.config;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.professions.Profession;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class ProfessionRestriction {
	@NonNull
	private final Profession profession;
	private final int level;

	public static ProfessionRestriction of(Profession profession, Integer level) {
		if (profession == null && level == null) {
			return null;
		}
		if (profession != null && level != null) {
			return new ProfessionRestriction(profession, level);
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", profession, level);
	}
}
