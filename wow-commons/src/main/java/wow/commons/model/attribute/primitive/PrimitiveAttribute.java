package wow.commons.model.attribute.primitive;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.condition.AttributeCondition;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-04
 */
public record PrimitiveAttribute(
		PrimitiveAttributeId id,
		double value,
		AttributeCondition condition,
		boolean levelScaled
) implements Attribute {
	public PrimitiveAttribute {
		Objects.requireNonNull(id);
		Objects.requireNonNull(condition);
	}

	public double getLevelScaledValue(int level) {
		return levelScaled ? value * level : value;
	}

	public PrimitiveAttribute scale(double factor) {
		return new PrimitiveAttribute(id, value * factor, condition, levelScaled);
	}
}
