package wow.minmax.model.config;

import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.spell.AbilityId;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public record ViewConfig(
		CharacterRestriction characterRestriction,
		TimeRestriction timeRestriction,
		double equivalentAmount,
		List<AbilityId> relevantSpells
) implements CharacterRestricted, TimeRestricted {
	public ViewConfig {
		Objects.requireNonNull(characterRestriction);
		Objects.requireNonNull(timeRestriction);
		Objects.requireNonNull(relevantSpells);
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
