package wow.commons.model.attributes;

import wow.commons.model.Percent;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-04
 */
public class Attributes implements AttributeSource {
	public static final Attributes EMPTY = new Attributes(List.of(), Map.of());

	private final List<PrimitiveAttribute> attributeList;
	private Map<AttributeId, List<ComplexAttribute>> complexAttributeList;

	private Map<AttributeId, Double> doubleCache;
	private Map<AttributeId, Percent> percentCache;

	public Attributes(List<PrimitiveAttribute> attributeList, Map<AttributeId, List<ComplexAttribute>> complexAttributeList) {
		this.attributeList = attributeList;
		this.complexAttributeList = complexAttributeList;
	}

	public Attributes(List<PrimitiveAttribute> attributeList) {
		this.attributeList = new ArrayList<>();
		this.attributeList.addAll(attributeList);
		this.complexAttributeList = Map.of();
	}

	public static Attributes of(AttributeId attributeId, double value) {
		return new Attributes(List.of(Attribute.of(attributeId, value)), Map.of());
	}

	public static Attributes of(AttributeId attributeId, Percent value) {
		return new Attributes(List.of(Attribute.of(attributeId, value)), Map.of());
	}

	public static Attributes of(ComplexAttribute complexAttribute) {
		return new Attributes(List.of(), Map.of(complexAttribute.getId(), List.of(complexAttribute)));
	}

	@Override
	public double getDouble(AttributeId attributeId) {
		ensureCache();
		return doubleCache.getOrDefault(attributeId, 0.0);
	}

	@Override
	public Percent getPercent(AttributeId attributeId) {
		ensureCache();
		return percentCache.getOrDefault(attributeId, Percent.ZERO);
	}

	private void ensureCache() {
		if (doubleCache != null) {
			return;
		}

		doubleCache = new EnumMap<>(AttributeId.class);
		percentCache = new EnumMap<>(AttributeId.class);

		for (Attribute attribute : attributeList) {
			if (attribute.getId().isDoubleAttribute()) {
				doubleCache.put(attribute.getId(), attribute.getDouble() + doubleCache.getOrDefault(attribute.getId(), 0.0));
			} else if (attribute.getId().isPercentAttribute()) {
				percentCache.put(attribute.getId(), attribute.getPercent().add(percentCache.getOrDefault(attribute.getId(), Percent.ZERO)));
			}
		}
	}

	@Override
	public List<PrimitiveAttribute> getPrimitiveAttributeList() {
		return attributeList;
	}

	@Override
	public Map<AttributeId, List<ComplexAttribute>> getComplexAttributeList() {
		return complexAttributeList;
	}

	@Override
	public boolean isEmpty() {
		return attributeList.isEmpty();
	}

	@Override
	public Attributes getAttributes() {
		return this;
	}

	public Attributes scale(double factor) {
		return new Attributes(
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
}
