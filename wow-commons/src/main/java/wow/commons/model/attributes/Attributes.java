package wow.commons.model.attributes;

import wow.commons.model.Percent;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-04
 */
public abstract class Attributes implements AttributeSource {
	public static final Attributes EMPTY = of(List.of(), Map.of());

	protected final List<PrimitiveAttribute> attributeList;
	private final Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeList;

	private Attributes(List<PrimitiveAttribute> attributeList, Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeList) {
		this.attributeList = attributeList;
		this.complexAttributeList = complexAttributeList;
	}

	public static Attributes of(List<PrimitiveAttribute> attributeList, Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeList) {
		if (attributeList.size() < 10) {
			return new NonCachedAttributes(attributeList, complexAttributeList);
		} else {
			return new CachedAttributes(attributeList, complexAttributeList);
		}
	}

	public static Attributes of(List<PrimitiveAttribute> attributeList) {
		return of(attributeList, Map.of());
	}

	public static Attributes of(PrimitiveAttribute... attributeList) {
		return of(List.of(attributeList), Map.of());
	}

	public static Attributes of(PrimitiveAttributeId attributeId, double value) {
		return of(List.of(Attribute.of(attributeId, value)), Map.of());
	}

	public static Attributes of(PrimitiveAttributeId attributeId, Percent value) {
		return of(List.of(Attribute.of(attributeId, value)), Map.of());
	}

	public static Attributes of(ComplexAttribute complexAttribute) {
		return of(List.of(), Map.of(complexAttribute.getId(), List.of(complexAttribute)));
	}

	@Override
	public List<PrimitiveAttribute> getPrimitiveAttributeList() {
		return attributeList;
	}

	@Override
	public Map<ComplexAttributeId, List<ComplexAttribute>> getComplexAttributeList() {
		return complexAttributeList;
	}

	@Override
	public boolean isEmpty() {
		return attributeList.isEmpty() && complexAttributeList.isEmpty();
	}

	@Override
	public Attributes getAttributes() {
		return this;
	}

	public Attributes scale(double factor) {
		return of(
				attributeList
				.stream()
				.map(attribute -> attribute.scale(factor))
				.collect(Collectors.toList()),
				complexAttributeList
		);
	}

	@Override
	public String toString() {
		return Stream.concat(
				attributeList.stream().sorted(
						Comparator.comparing(PrimitiveAttribute::getId)
								.thenComparing(x -> x.getCondition().toString())
				),
				complexAttributeList.values().stream().flatMap(Collection::stream)
		)
		 .map(Object::toString)
		 .collect(Collectors.joining(", "));
	}

	private static class NonCachedAttributes extends Attributes {
		private NonCachedAttributes(List<PrimitiveAttribute> attributeList, Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeList) {
			super(attributeList, complexAttributeList);
		}
	}

	private static class CachedAttributes extends Attributes {
		private Map<PrimitiveAttributeId, Double> doubleCache;

		private CachedAttributes(List<PrimitiveAttribute> attributeList, Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeList) {
			super(attributeList, complexAttributeList);
		}

		@Override
		public double getDouble(PrimitiveAttributeId attributeId) {
			ensureCache();
			return doubleCache.getOrDefault(attributeId, 0.0);
		}

		private void ensureCache() {
			if (doubleCache != null) {
				return;
			}

			doubleCache = new EnumMap<>(PrimitiveAttributeId.class);

			for (PrimitiveAttribute attribute : attributeList) {
				PrimitiveAttributeId id = attribute.getId();
				doubleCache.put(id, attribute.getDouble() + doubleCache.getOrDefault(id, 0.0));
			}
		}
	}
}
