package wow.commons.model.attribute;

import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-04
 */
public record Attributes(List<PrimitiveAttribute> primitiveAttributes) {
	public static final Attributes EMPTY = new Attributes(List.of());

	public Attributes {
		Objects.requireNonNull(primitiveAttributes);
	}

	public static Attributes of(List<PrimitiveAttribute> primitiveAttributes) {
		return new Attributes(primitiveAttributes);
	}

	public static Attributes of(PrimitiveAttribute... attributeList) {
		return of(List.of(attributeList));
	}

	public static Attributes of(PrimitiveAttributeId attributeId, double value) {
		return of(List.of(Attribute.of(attributeId, value)));
	}

	public static Attributes of(PrimitiveAttributeId attributeId, double value, AttributeCondition condition) {
		return of(List.of(Attribute.of(attributeId, value, condition)));
	}

	public boolean isEmpty() {
		return primitiveAttributes.isEmpty();
	}

	public Attributes scale(double factor) {
		if (isEmpty() || factor == 0) {
			return EMPTY;
		}
		var scaledList = primitiveAttributes.stream()
				.map(primitiveAttribute -> primitiveAttribute.scale(factor))
				.toList();
		return of(scaledList);
	}
}
