package wow.commons.model.attributes;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;

/**
 * User: POlszewski
 * Date: 2021-10-07
 */
public sealed interface Attribute permits PrimitiveAttribute, ComplexAttribute {
	static PrimitiveAttribute of(PrimitiveAttributeId id, double value) {
		return of(id, value, AttributeCondition.EMPTY);
	}

	static PrimitiveAttribute of(PrimitiveAttributeId id, double value, AttributeCondition condition) {
		return new PrimitiveAttribute(id, value, condition);
	}

	static PrimitiveAttribute of(PrimitiveAttributeId id, Percent value) {
		return of(id, value, AttributeCondition.EMPTY);
	}

	static PrimitiveAttribute of(PrimitiveAttributeId id, Percent value, AttributeCondition condition) {
		return new PrimitiveAttribute(id, value.value(), condition);
	}

	static PrimitiveAttribute of(PrimitiveAttributeId id, Duration value) {
		return of(id, value, AttributeCondition.EMPTY);
	}

	static PrimitiveAttribute of(PrimitiveAttributeId id, Duration value, AttributeCondition condition) {
		return new PrimitiveAttribute(id, value.getSeconds(), condition);
	}

	static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, double value) {
		return ofNullable(id, value, AttributeCondition.EMPTY);
	}

	static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, double value, AttributeCondition condition) {
		return value != 0 ? of(id, value, condition) : null;
	}

	static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, Percent value) {
		return ofNullable(id, value, AttributeCondition.EMPTY);
	}

	static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, Percent value, AttributeCondition condition) {
		return value != null && !value.isZero() ? of(id, value, condition) : null;
	}

	static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, Duration value) {
		return ofNullable(id, value, AttributeCondition.EMPTY);
	}

	static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, Duration value, AttributeCondition condition) {
		return value != null && !value.isZero() ? of(id, value, condition) : null;
	}

	AttributeId id();

	AttributeCondition condition();

	default boolean hasCondition() {
		return !condition().isEmpty();
	}

	Attribute attachCondition(AttributeCondition condition);
}
