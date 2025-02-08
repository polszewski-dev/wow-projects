package wow.commons.model.attribute;

import wow.commons.model.attribute.condition.AttributeCondition;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-10-07
 */
public record Attribute(
		AttributeId id,
		double value,
		AttributeCondition condition,
		AttributeScaling scaling
) {
	public Attribute {
		Objects.requireNonNull(id);
		Objects.requireNonNull(condition);
		Objects.requireNonNull(scaling);
	}

	public static Attribute of(AttributeId id, double value) {
		return of(id, value, AttributeCondition.EMPTY, AttributeScaling.NONE);
	}

	public static Attribute of(AttributeId id, double value, AttributeCondition condition) {
		return of(id, value, condition, AttributeScaling.NONE);
	}

	public static Attribute of(AttributeId id, double value, AttributeCondition condition, AttributeScaling scaling) {
		return new Attribute(id, value, condition, scaling);
	}

	public double getScaledValue(AttributeScalingParams params) {
		return scaling.getScaledValue(value, params);
	}

	public Attribute negate() {
		return new Attribute(id, -value, condition, scaling);
	}

	public Attribute scale(double factor) {
		return new Attribute(id, value * factor, condition, scaling);
	}

	public Attribute intScale(double factor) {
		return new Attribute(id, (int) (value * factor), condition, scaling);
	}

	public boolean hasCondition() {
		return !condition().isEmpty();
	}
}
