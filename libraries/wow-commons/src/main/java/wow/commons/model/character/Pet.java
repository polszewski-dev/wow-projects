package wow.commons.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.pve.GameVersion;

/**
 * User: POlszewski
 * Date: 2023-05-15
 */
@AllArgsConstructor
@Getter
public class Pet implements Described, CharacterRestricted {
	@NonNull
	private final PetType petType;

	@NonNull
	private final Description description;

	@NonNull
	private final CharacterRestriction characterRestriction;

	@NonNull
	private final GameVersion gameVersion;

	@Override
	public String toString() {
		return getName();
	}
}
