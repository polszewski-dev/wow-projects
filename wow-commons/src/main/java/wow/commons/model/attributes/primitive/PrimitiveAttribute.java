package wow.commons.model.attributes.primitive;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.util.PrimitiveAttributeFormatter;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-04
 */
public record PrimitiveAttribute(
		PrimitiveAttributeId id,
		double value,
		AttributeCondition condition
) implements Attribute {
	public PrimitiveAttribute {
		Objects.requireNonNull(id);
		Objects.requireNonNull(condition);
	}

	public double getDouble() {
		return value;
	}

	public Percent getPercent() {
		return Percent.of(value);
	}

	public Duration getDuration() {
		return Duration.seconds(value);
	}

	@Override
	public PrimitiveAttribute attachCondition(AttributeCondition condition) {
		return new PrimitiveAttribute(id, value, condition);
	}

	public PrimitiveAttribute scale(double factor) {
		double scaledValue = factor * value;
		return new PrimitiveAttribute(id, scaledValue, condition);
	}

	public PrimitiveAttribute negate() {
		return new PrimitiveAttribute(id, -value, condition);
	}

	@Override
	public String toString() {
		return PrimitiveAttributeFormatter.format(this);
	}
}
