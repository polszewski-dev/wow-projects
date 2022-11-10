package wow.commons.model.attributes;

import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;

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
		if ((id = PrimitiveAttributeId.tryParse(value)) != null) {
			return id;
		}
		if ((id = ComplexAttributeId.tryParse(value)) != null) {
			return id;
		}
		throw new IllegalArgumentException(value);
	}

	boolean isPrimitiveAttribute();

	default boolean isComplexAttribute() {
		return !isPrimitiveAttribute();
	}

	int getSortOrder();
}
