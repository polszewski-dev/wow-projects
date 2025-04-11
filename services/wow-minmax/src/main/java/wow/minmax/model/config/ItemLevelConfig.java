package wow.minmax.model.config;

import wow.character.model.equipment.ItemLevelFilter;
import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-04-11
 */
public record ItemLevelConfig(
		CharacterRestriction characterRestriction,
		TimeRestriction timeRestriction,
		ItemLevelFilter filter
) implements CharacterRestricted, TimeRestricted {
	public ItemLevelConfig {
		Objects.requireNonNull(characterRestriction);
		Objects.requireNonNull(timeRestriction);
		Objects.requireNonNull(filter);
	}

	@Override
	public CharacterRestriction getCharacterRestriction() {
		return characterRestriction;
	}

	@Override
	public TimeRestriction getTimeRestriction() {
		return timeRestriction;
	}
}
