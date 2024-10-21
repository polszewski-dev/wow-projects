package wow.commons.model.spell;

import wow.commons.model.Percent;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-16
 */
public record Coefficient(Percent value, SpellSchool school) {
	public static final Coefficient NONE = new Coefficient(Percent.ZERO, null);

	public Coefficient {
		Objects.requireNonNull(value);
	}
}
