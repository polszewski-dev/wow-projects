package wow.commons.model.racial;

import lombok.Getter;
import wow.commons.model.config.*;
import wow.commons.model.effect.Effect;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
@Getter
public class Racial implements Described, TimeRestricted, CharacterRestricted {
	private final String id;
	private final Description description;
	private final TimeRestriction timeRestriction;
	private final CharacterRestriction characterRestriction;
	private List<Effect> effects;

	public Racial(Description description, TimeRestriction timeRestriction, CharacterRestriction characterRestriction) {
		this.id = description.name();
		this.description = description;
		this.timeRestriction = timeRestriction;
		this.characterRestriction = characterRestriction;
	}

	@Override
	public String toString() {
		return getName();
	}
}
