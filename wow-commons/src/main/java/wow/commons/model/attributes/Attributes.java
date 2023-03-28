package wow.commons.model.attributes;

import lombok.Getter;
import wow.commons.model.Percent;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-04
 */
@Getter
public class Attributes implements AttributeSource {
	public static final Attributes EMPTY = of(List.of(), Map.of());

	private final List<PrimitiveAttribute> primitiveAttributes;
	private final Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeMap;

	private Attributes(List<PrimitiveAttribute> primitiveAttributes, Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeMap) {
		this.primitiveAttributes = primitiveAttributes;
		this.complexAttributeMap = complexAttributeMap;
	}

	public static Attributes of(List<PrimitiveAttribute> primitiveAttributes, Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeMap) {
		return new Attributes(primitiveAttributes, complexAttributeMap);
	}

	public static Attributes of(List<PrimitiveAttribute> primitiveAttributes) {
		return of(primitiveAttributes, Map.of());
	}

	public static Attributes of(PrimitiveAttribute... attributeList) {
		return of(List.of(attributeList), Map.of());
	}

	public static Attributes of(PrimitiveAttributeId attributeId, double value) {
		return of(List.of(Attribute.of(attributeId, value)), Map.of());
	}

	public static Attributes of(PrimitiveAttributeId attributeId, double value, AttributeCondition condition) {
		return of(List.of(Attribute.of(attributeId, value, condition)), Map.of());
	}

	public static Attributes of(PrimitiveAttributeId attributeId, Percent value) {
		return of(List.of(Attribute.of(attributeId, value)), Map.of());
	}

	public static Attributes of(ComplexAttribute complexAttribute) {
		return of(List.of(), Map.of(complexAttribute.getId(), List.of(complexAttribute)));
	}

	@Override
	public boolean isEmpty() {
		return primitiveAttributes.isEmpty() && complexAttributeMap.isEmpty();
	}

	@Override
	public Attributes getAttributes() {
		return this;
	}

	public Attributes scale(double factor) {
		return of(
				primitiveAttributes
				.stream()
				.map(attribute -> attribute.scale(factor))
				.toList(),
				complexAttributeMap
		);
	}

	@Override
	public String toString() {
		return Stream.concat(
						primitiveAttributes.stream().sorted(
						Comparator.comparing(PrimitiveAttribute::getId)
								.thenComparing(x -> x.getCondition().toString())
				),
				complexAttributeMap.values().stream().flatMap(Collection::stream)
		)
		 .map(Object::toString)
		 .collect(Collectors.joining(", "));
	}
}
