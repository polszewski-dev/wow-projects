package wow.commons.model.attributes;

import wow.commons.model.Percent;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.primitive.DoubleAttributeId;
import wow.commons.model.attributes.primitive.PercentAttributeId;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-04
 */
public abstract class Attributes implements AttributeSource {
	public static final Attributes EMPTY = of(List.of(), Map.of());

	protected final List<? extends PrimitiveAttribute> attributeList;
	private final Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeList;

	private Attributes(List<? extends PrimitiveAttribute> attributeList, Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeList) {
		this.attributeList = attributeList;
		this.complexAttributeList = complexAttributeList;
	}

	public static Attributes of(List<? extends PrimitiveAttribute> attributeList, Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeList) {
		if (attributeList.size() < 10) {
			return new NonCachedAttributes(attributeList, complexAttributeList);
		} else {
			return new CachedAttributes(attributeList, complexAttributeList);
		}
	}

	public static Attributes of(List<? extends PrimitiveAttribute> attributeList) {
		return of(attributeList, Map.of());
	}

	public static Attributes of(DoubleAttributeId attributeId, double value) {
		return of(List.of(Attribute.of(attributeId, value)), Map.of());
	}

	public static Attributes of(PercentAttributeId attributeId, Percent value) {
		return of(List.of(Attribute.of(attributeId, value)), Map.of());
	}

	public static Attributes of(ComplexAttribute complexAttribute) {
		return of(List.of(), Map.of(complexAttribute.getId(), List.of(complexAttribute)));
	}

	@Override
	public List<PrimitiveAttribute> getPrimitiveAttributeList() {
		return (List<PrimitiveAttribute>)attributeList;
	}

	@Override
	public Map<ComplexAttributeId, List<ComplexAttribute>> getComplexAttributeList() {
		return complexAttributeList;
	}

	@Override
	public boolean isEmpty() {
		return attributeList.isEmpty();//TODO bug?
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
				attributeList.stream(),
				complexAttributeList.values().stream().flatMap(Collection::stream)
		)
		 .map(Object::toString)
		 .collect(Collectors.joining(", "));
	}

	private static class NonCachedAttributes extends Attributes {
		private NonCachedAttributes(List<? extends PrimitiveAttribute> attributeList, Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeList) {
			super(attributeList, complexAttributeList);
		}
	}

	private static class CachedAttributes extends Attributes {
		private Map<DoubleAttributeId, Double> doubleCache;
		private Map<PercentAttributeId, Percent> percentCache;

		private CachedAttributes(List<? extends PrimitiveAttribute> attributeList, Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeList) {
			super(attributeList, complexAttributeList);
		}

		@Override
		public double getDouble(DoubleAttributeId attributeId) {
			ensureCache();
			return doubleCache.getOrDefault(attributeId, 0.0);
		}

		@Override
		public Percent getPercent(PercentAttributeId attributeId) {
			ensureCache();
			return percentCache.getOrDefault(attributeId, Percent.ZERO);
		}

		private void ensureCache() {
			if (doubleCache != null) {
				return;
			}

			doubleCache = new EnumMap<>(DoubleAttributeId.class);
			percentCache = new EnumMap<>(PercentAttributeId.class);

			for (Attribute attribute : attributeList) {
				if (attribute.getId().isDoubleAttribute()) {
					DoubleAttributeId id = (DoubleAttributeId) attribute.getId();
					doubleCache.put(id, attribute.getDouble() + doubleCache.getOrDefault(id, 0.0));
				} else if (attribute.getId().isPercentAttribute()) {
					PercentAttributeId id = (PercentAttributeId) attribute.getId();
					percentCache.put(id, attribute.getPercent().add(percentCache.getOrDefault(id, Percent.ZERO)));
				}
			}
		}
	}
}
