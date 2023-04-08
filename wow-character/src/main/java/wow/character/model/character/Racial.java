package wow.character.model.character;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.config.impl.ConfigurationElementWithAttributesImpl;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Racial extends ConfigurationElementWithAttributesImpl<String> {
	private final Race race;

	public Racial(Description description, TimeRestriction timeRestriction, CharacterRestriction characterRestriction, Race race) {
		super(description.getName(), description, timeRestriction, characterRestriction);
		this.race = race;
	}

	@Override
	protected SpecialAbilitySource getSpecialAbilitySource() {
		return new RacialSource(this);
	}
}
