package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.config.Description;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class RacialSource implements SpecialAbilitySource, Comparable<RacialSource> {
	private final Racial racial;

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
