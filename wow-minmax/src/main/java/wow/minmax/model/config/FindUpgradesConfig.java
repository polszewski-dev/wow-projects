package wow.minmax.model.config;

import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.Enchant;

import java.util.Objects;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-05-26
 */
public record FindUpgradesConfig(
		CharacterRestriction characterRestriction,
		TimeRestriction timeRestriction,
		Set<String> enchantNames
) implements CharacterRestricted, TimeRestricted {
	public FindUpgradesConfig {
		Objects.requireNonNull(characterRestriction);
		Objects.requireNonNull(timeRestriction);
		Objects.requireNonNull(enchantNames);
	}

	@Override
	public CharacterRestriction getCharacterRestriction() {
		return characterRestriction;
	}

	@Override
	public TimeRestriction getTimeRestriction() {
		return timeRestriction;
	}

	public boolean isIncluded(Enchant x) {
		return enchantNames.contains(x.getName());
	}
}
