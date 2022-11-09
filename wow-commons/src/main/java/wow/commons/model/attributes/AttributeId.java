package wow.commons.model.attributes;

import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.primitive.BooleanAttributeId;
import wow.commons.model.attributes.primitive.DoubleAttributeId;
import wow.commons.model.attributes.primitive.DurationAttributeId;
import wow.commons.model.attributes.primitive.PercentAttributeId;

/**
 * User: POlszewski
 * Date: 2021-03-04
 */
public interface AttributeId {
	static AttributeId parse(String value) {
		if (value == null) {
			return null;
		}
		AttributeId id;
		if ((id = DoubleAttributeId.tryParse(value)) != null) {
			return id;
		}
		if ((id = PercentAttributeId.tryParse(value)) != null) {
			return id;
		}
		if ((id = DurationAttributeId.tryParse(value)) != null) {
			return id;
		}
		if ((id = BooleanAttributeId.tryParse(value)) != null) {
			return id;
		}
		if ((id = ComplexAttributeId.tryParse(value)) != null) {
			return id;
		}
		throw new IllegalArgumentException(value);
	}

	default boolean isDoubleAttribute() {
		return false;
	}

	default boolean isPercentAttribute() {
		return false;
	}

	default boolean isBooleanAttribute() {
		return false;
	}

	default boolean isDurationAttribute() {
		return false;
	}

	default boolean isComplexAttribute() {
		return false;
	}

	default boolean isScalarAttribute() {
		return isDoubleAttribute() || isPercentAttribute() || isDurationAttribute();
	}

	default boolean isPrimitiveAttribute() {
		return !isComplexAttribute();
	}

	int getSortOrder();
}
