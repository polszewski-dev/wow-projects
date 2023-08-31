package wow.commons.model.attribute.complex.special;

import wow.commons.model.Duration;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.util.AttributesBuilder;

import java.util.Objects;

import static wow.commons.util.PrimitiveAttributeFormatter.getConditionString;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
public record ProcAbility(
		ProcEvent event,
		Attributes attributes,
		Duration duration,
		Duration cooldown,
		String line,
		AttributeCondition condition,
		SpecialAbilitySource source
) implements SpecialAbility {
	public ProcAbility {
		Objects.requireNonNull(event);
		Objects.requireNonNull(attributes);
		Objects.requireNonNull(duration);
		Objects.requireNonNull(cooldown);
		Objects.requireNonNull(line);
		Objects.requireNonNull(condition);

		attributes = AttributesBuilder.attachCondition(attributes, condition);
	}

	@Override
	public int priority() {
		return 3;
	}

	@Override
	public ProcAbility attachCondition(AttributeCondition condition) {
		return new ProcAbility(event, attributes, duration, cooldown, line, condition, source);
	}

	@Override
	public ProcAbility attachSource(SpecialAbilitySource source) {
		return new ProcAbility(event, attributes, duration, cooldown, line, condition, source);
	}

	@Override
	public String toString() {
		return "(event: %s, chance: %s, %s | %s/%s)".formatted(event.type(), event.chance(), attributes, duration, cooldown.isZero() ? "-" : cooldown)
				+ getConditionString(condition);
	}
}
