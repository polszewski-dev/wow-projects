package wow.commons.model.attributes.complex.special;

import wow.commons.model.Duration;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.condition.AttributeCondition;
import wow.commons.util.AttributesBuilder;

import java.util.Objects;

import static wow.commons.util.PrimitiveAttributeFormatter.getConditionString;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
public record OnUseAbility(
		Attributes attributes,
		Duration duration,
		Duration cooldown,
		String line,
		AttributeCondition condition,
		SpecialAbilitySource source,
		Attributes equivalent
) implements SpecialAbility {
	public OnUseAbility {
		Objects.requireNonNull(attributes);
		Objects.requireNonNull(duration);
		Objects.requireNonNull(cooldown);
		Objects.requireNonNull(line);
		Objects.requireNonNull(condition);

		attributes = AttributesBuilder.attachCondition(attributes, condition);

		if (equivalent == null) {
			double factor = duration.divideBy(cooldown);
			equivalent = attributes.scale(factor);
		}
	}

	@Override
	public int priority() {
		return 2;
	}

	@Override
	public OnUseAbility attachCondition(AttributeCondition condition) {
		return new OnUseAbility(attributes, duration, cooldown, line, condition, source, equivalent);
	}

	@Override
	public OnUseAbility attachSource(SpecialAbilitySource source) {
		return new OnUseAbility(attributes, duration, cooldown, line, condition, source, equivalent);
	}

	@Override
	public String toString() {
		return "(%s | %s/%s)".formatted(attributes, duration, cooldown) + getConditionString(condition);
	}
}
