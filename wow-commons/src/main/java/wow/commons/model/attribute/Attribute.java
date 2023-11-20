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
		boolean levelScaled
) {
	public Attribute {
		Objects.requireNonNull(id);
		Objects.requireNonNull(condition);
	}

	public static Attribute of(AttributeId id, double value) {
		return of(id, value, AttributeCondition.EMPTY, false);
	}

	public static Attribute of(AttributeId id, double value, AttributeCondition condition) {
		return of(id, value, condition, false);
	}

	public static Attribute of(AttributeId id, double value, AttributeCondition condition, boolean levelScaled) {
		return new Attribute(id, value, condition, levelScaled);
	}

	public double getLevelScaledValue(int level) {
		return levelScaled ? value * level : value;
	}

	public Attribute negate() {
		return new Attribute(id, -value, condition, levelScaled);
	}

	public Attribute scale(double factor) {
		return new Attribute(id, value * factor, condition, levelScaled);
	}

	public boolean hasCondition() {
		return !condition().isEmpty();
	}
}
