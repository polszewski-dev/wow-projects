package wow.commons.model.attribute;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-04
 */
public record Attributes(List<Attribute> list) {
	public static final Attributes EMPTY = new Attributes(List.of());

	public Attributes {
		Objects.requireNonNull(list);
	}

	public static Attributes of(List<Attribute> list) {
		return new Attributes(list);
	}

	public static Attributes of(Attribute... attributeList) {
		return of(List.of(attributeList));
	}

	public static Attributes of(AttributeId attributeId, double value) {
		return of(List.of(Attribute.of(attributeId, value)));
	}

	public static Attributes of(AttributeId attributeId, double value, AttributeCondition condition) {
		return of(List.of(Attribute.of(attributeId, value, condition)));
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Attributes scale(double factor) {
		if (isEmpty() || factor == 0) {
			return EMPTY;
		}
		var scaledList = list.stream()
				.map(attribute -> attribute.scale(factor))
				.toList();
		return of(scaledList);
	}
}
