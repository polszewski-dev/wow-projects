package wow.commons.model.attribute.complex.special;

import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.util.AttributesBuilder;

import java.util.Objects;

import static wow.commons.util.PrimitiveAttributeFormatter.getConditionString;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
public record EquivalentAbility(
		Attributes attributes,
		String line,
		AttributeCondition condition,
		SpecialAbilitySource source
) implements SpecialAbility {
	public EquivalentAbility {
		Objects.requireNonNull(attributes);
		Objects.requireNonNull(line);
		Objects.requireNonNull(condition);

		if (!attributes.getComplexAttributeMap().isEmpty()) {
			throw new IllegalArgumentException("Equivalent can't have complex attributes");
		}

		attributes = AttributesBuilder.attachCondition(attributes, condition);
	}

	@Override
	public int priority() {
		return 1;
	}

	@Override
	public EquivalentAbility attachCondition(AttributeCondition condition) {
		return new EquivalentAbility(attributes, line, condition, source);
	}

	@Override
	public EquivalentAbility attachSource(SpecialAbilitySource source) {
		return new EquivalentAbility(attributes, line, condition, source);
	}

	@Override
	public String toString() {
		return attributes + getConditionString(condition);
	}
}
