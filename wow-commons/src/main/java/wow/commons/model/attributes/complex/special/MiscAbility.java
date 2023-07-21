package wow.commons.model.attributes.complex.special;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.SpecialAbilitySource;

import java.util.Objects;

import static wow.commons.util.PrimitiveAttributeFormatter.getConditionString;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
public record MiscAbility(
		String line,
		AttributeCondition condition,
		SpecialAbilitySource source
) implements SpecialAbility {
	public MiscAbility {
		Objects.requireNonNull(line);
		Objects.requireNonNull(condition);
	}

	@Override
	public int priority() {
		return 5;
	}

	@Override
	public Attributes attributes() {
		return Attributes.EMPTY;
	}

	@Override
	public MiscAbility attachCondition(AttributeCondition condition) {
		return new MiscAbility(line, condition, source);
	}

	@Override
	public MiscAbility attachSource(SpecialAbilitySource source) {
		return new MiscAbility(line, condition, source);
	}

	@Override
	public String toString() {
		return line + getConditionString(condition);
	}
}
