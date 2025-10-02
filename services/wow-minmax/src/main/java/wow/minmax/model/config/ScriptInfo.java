package wow.minmax.model.config;

import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;

/**
 * User: POlszewski
 * Date: 2025-09-26
 */
public record ScriptInfo(
		CharacterRestriction characterRestriction,
		TimeRestriction timeRestriction,
		String name,
		String path
)  implements CharacterRestricted, TimeRestricted {
	@Override
	public CharacterRestriction getCharacterRestriction() {
		return characterRestriction;
	}

	@Override
	public TimeRestriction getTimeRestriction() {
		return timeRestriction;
	}
}
