package wow.minmax.model.config;

import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-08-06
 */
public record CharacterFeatureConfig(
		CharacterRestriction characterRestriction,
		TimeRestriction timeRestriction,
		CharacterFeature feature
) implements CharacterRestricted, TimeRestricted {
	public CharacterFeatureConfig {
		Objects.requireNonNull(characterRestriction);
		Objects.requireNonNull(feature);
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
