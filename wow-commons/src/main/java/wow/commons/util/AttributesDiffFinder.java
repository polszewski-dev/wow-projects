package wow.commons.util;

import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.primitive.*;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
public class AttributesDiffFinder {
	private final Attributes attributes1;
	private final Attributes attributes2;

	private final Map<String, DoubleCollector> doubleAttributes = new HashMap<>();
	private final Map<String, PercentCollector> percentAttributes = new HashMap<>();

	public AttributesDiffFinder(Attributes attributes1, Attributes attributes2) {
		this.attributes1 = attributes1;
		this.attributes2 = attributes2;
	}

	public AttributesDiff getDiff() {
		for (var attribute : attributes1.getPrimitiveAttributeList()) {
			addPrimitiveAttribute(attribute);
		}

		for (var attribute : attributes2.getPrimitiveAttributeList()) {
			subtractPrimitiveAttribute(attribute);
		}

		AttributesDiff result = new AttributesDiff();

		addPrimitiveAttributeDiff(result);
		addComplexAttributeDiff(result, attributes1, attributes2);

		return result;
	}

	private void addPrimitiveAttribute(PrimitiveAttribute attribute) {
		AttributeId id = attribute.getId();

		if (id.isDoubleAttribute()) {
			getDoubleCollector(attribute).add((DoubleAttribute) attribute);
		} else if (id.isPercentAttribute()) {
			getPercentCollector(attribute).add((PercentAttribute) attribute);
		} else {
			throw new IllegalArgumentException(attribute.toString());
		}
	}

	private void subtractPrimitiveAttribute(PrimitiveAttribute attribute) {
		AttributeId id = attribute.getId();

		if (id.isDoubleAttribute()) {
			getDoubleCollector(attribute).subtract((DoubleAttribute) attribute);
		} else if (id.isPercentAttribute()) {
			getPercentCollector(attribute).subtract((PercentAttribute) attribute);
		} else {
			throw new IllegalArgumentException(attribute.toString());
		}
	}

	private DoubleCollector getDoubleCollector(PrimitiveAttribute attribute) {
		String key = getKey(attribute);
		return doubleAttributes.computeIfAbsent(key, x -> new DoubleCollector((DoubleAttribute) attribute));
	}

	private PercentCollector getPercentCollector(PrimitiveAttribute attribute) {
		String key = getKey(attribute);
		return percentAttributes.computeIfAbsent(key, x -> new PercentCollector((PercentAttribute) attribute));
	}

	private void addPrimitiveAttributeDiff(AttributesDiff result) {
		List<PrimitiveAttribute> attributes = new ArrayList<>();

		for (DoubleCollector collector : doubleAttributes.values()) {
			attributes.add(collector.getResult());
		}

		for (PercentCollector collector : percentAttributes.values()) {
			attributes.add(collector.getResult());
		}

		attributes.removeIf(Objects::isNull);

		attributes.sort(
				Comparator.<PrimitiveAttribute>comparingInt(x -> x.getId().getSortOrder())
						.thenComparing(x -> x.getCondition() != null ? x.getCondition().toString() : "")
		);

		result.setAttributes(Attributes.of(attributes));
	}

	private static void addComplexAttributeDiff(AttributesDiff result, Attributes attributes1, Attributes attributes2) {
		for (var entry : attributes1.getComplexAttributeList().entrySet()) {
			ensureList(entry.getKey(), result.getAddedAbilities()).addAll(entry.getValue());
		}

		for (var entry : attributes2.getComplexAttributeList().entrySet()) {
			ensureList(entry.getKey(), result.getAddedAbilities()).removeAll(entry.getValue());
		}

		for (var entry : attributes2.getComplexAttributeList().entrySet()) {
			ensureList(entry.getKey(), result.getRemovedAbilities()).addAll(entry.getValue());
		}

		for (var entry : attributes1.getComplexAttributeList().entrySet()) {
			ensureList(entry.getKey(), result.getRemovedAbilities()).removeAll(entry.getValue());
		}
	}

	private static List<ComplexAttribute> ensureList(ComplexAttributeId key, Map<ComplexAttributeId, List<ComplexAttribute>> list) {
		return list.computeIfAbsent(key, x -> new ArrayList<>());
	}

	private static String getKey(PrimitiveAttribute attribute) {
		return attribute.getId() + " " + attribute.getCondition();
	}

	private static class DoubleCollector {
		private final DoubleAttributeId id;
		private final AttributeCondition condition;
		private double value;

		DoubleCollector(DoubleAttribute prototype) {
			this.id = prototype.getId();
			this.condition = prototype.getCondition();
		}

		void add(DoubleAttribute attribute) {
			this.value += attribute.getDouble();
		}

		void subtract(DoubleAttribute attribute) {
			this.value -= attribute.getDouble();
		}

		DoubleAttribute getResult() {
			return Attribute.ofNullable(id, value, condition);
		}
	}

	private static class PercentCollector {
		private final PercentAttributeId id;
		private final AttributeCondition condition;
		private Percent value = Percent.ZERO;

		PercentCollector(PercentAttribute prototype) {
			this.id = prototype.getId();
			this.condition = prototype.getCondition();
		}

		void add(PercentAttribute attribute) {
			this.value = value.add(attribute.getPercent());
		}

		void subtract(PercentAttribute attribute) {
			this.value = value.subtract(attribute.getPercent());
		}

		PercentAttribute getResult() {
			return Attribute.ofNullable(id, value, condition);
		}
	}
}
