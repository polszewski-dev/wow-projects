package wow.commons.model.attribute;

import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;

/**
 * User: POlszewski
 * Date: 2021-10-07
 */
public sealed interface Attribute permits PrimitiveAttribute {
	static PrimitiveAttribute of(PrimitiveAttributeId id, double value) {
		return of(id, value, AttributeCondition.EMPTY, false);
	}

	static PrimitiveAttribute of(PrimitiveAttributeId id, double value, AttributeCondition condition) {
		return of(id, value, condition, false);
	}

	static PrimitiveAttribute of(PrimitiveAttributeId id, double value, AttributeCondition condition, boolean levelScaled) {
		return new PrimitiveAttribute(id, value, condition, levelScaled);
	}

	AttributeId id();

	AttributeCondition condition();

	default boolean hasCondition() {
		return !condition().isEmpty();
	}
}
