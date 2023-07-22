package wow.character.model.character;

import wow.commons.model.attributes.complex.special.SpecialAbilitySource;
import wow.commons.model.config.Description;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record RacialSource(Racial racial) implements SpecialAbilitySource, Comparable<RacialSource> {
	@Override
	public Description getDescription() {
		return racial.getDescription();
	}

	@Override
	public int getPriority() {
		return 10;
	}

	@Override
	public int compareTo(RacialSource o) {
		return this.racial.getName().compareTo(o.racial.getName());
	}
}
