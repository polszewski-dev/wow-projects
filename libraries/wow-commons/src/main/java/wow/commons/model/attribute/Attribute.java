package wow.commons.model.attribute;

import java.util.Objects;

import static wow.commons.model.attribute.AttributeTarget.OWNER;

/**
 * User: POlszewski
 * Date: 2021-10-07
 */
public record Attribute(
		AttributeTarget target,
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
		return of(OWNER, id, value, AttributeCondition.EMPTY, AttributeScaling.NONE);
	}

	public static Attribute of(AttributeTarget target, AttributeId id, double value) {
		return of(target, id, value, AttributeCondition.EMPTY, AttributeScaling.NONE);
	}

	public static Attribute of(AttributeId id, double value, AttributeCondition condition) {
		return of(OWNER, id, value, condition, AttributeScaling.NONE);
	}

	public static Attribute of(AttributeTarget target, AttributeId id, double value, AttributeCondition condition) {
		return of(target, id, value, condition, AttributeScaling.NONE);
	}

	public static Attribute of(AttributeId id, double value, AttributeCondition condition, AttributeScaling scaling) {
		return of(OWNER, id, value, condition, scaling);
	}

	public static Attribute of(AttributeTarget target, AttributeId id, double value, AttributeCondition condition, AttributeScaling scaling) {
		return new Attribute(target, id, value, condition, scaling);
	}

	public double getScaledValue(AttributeScalingParams params) {
		return scaling.getScaledValue(value, params);
	}

	public Attribute scale(double factor) {
		return new Attribute(target, id, value * factor, condition, scaling);
	}

	public Attribute intScale(double factor) {
		return new Attribute(target, id, (int) (value * factor), condition, scaling);
	}

	public boolean hasCondition() {
		return !condition().isEmpty();
	}
}
