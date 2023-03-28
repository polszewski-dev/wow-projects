package wow.commons.util;

import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
public class AttributesDiffFinder {
	private final Attributes attributes1;
	private final Attributes attributes2;

	private final Map<String, PrimitiveCollector> collectors = new HashMap<>();

	public AttributesDiffFinder(Attributes attributes1, Attributes attributes2) {
		this.attributes1 = attributes1;
		this.attributes2 = attributes2;
	}

	public AttributesDiff getDiff() {
		AttributesDiff result = new AttributesDiff();
		addPrimitiveAttributeDiff(result);
		addComplexAttributeDiff(result);
		return result;
	}

	private void addPrimitiveAttributeDiff(AttributesDiff result) {
		List<PrimitiveAttribute> attributes = getPrimitiveAttributeDiff();
		result.setAttributes(Attributes.of(attributes));
	}

	private List<PrimitiveAttribute> getPrimitiveAttributeDiff() {
		for (var attribute : attributes1.getPrimitiveAttributes()) {
			getDoubleCollector(attribute).add(attribute);
		}

		for (var attribute : attributes2.getPrimitiveAttributes()) {
			getDoubleCollector(attribute).subtract(attribute);
		}

		List<PrimitiveAttribute> attributes = new ArrayList<>();

		for (PrimitiveCollector collector : collectors.values()) {
			attributes.add(collector.getResult());
		}

		attributes.removeIf(Objects::isNull);

		return attributes;
	}

	private void addComplexAttributeDiff(AttributesDiff result) {
		for (var entry : attributes1.getComplexAttributeMap().entrySet()) {
			ensureList(entry.getKey(), result.getAddedAbilities()).addAll(entry.getValue());
		}

		for (var entry : attributes2.getComplexAttributeMap().entrySet()) {
			ensureList(entry.getKey(), result.getAddedAbilities()).removeAll(entry.getValue());
		}

		for (var entry : attributes2.getComplexAttributeMap().entrySet()) {
			ensureList(entry.getKey(), result.getRemovedAbilities()).addAll(entry.getValue());
		}

		for (var entry : attributes1.getComplexAttributeMap().entrySet()) {
			ensureList(entry.getKey(), result.getRemovedAbilities()).removeAll(entry.getValue());
		}
	}

	private PrimitiveCollector getDoubleCollector(PrimitiveAttribute attribute) {
		String key = attribute.getId() + " " + attribute.getCondition();
		return collectors.computeIfAbsent(key, x -> new PrimitiveCollector(attribute));
	}

	private static List<ComplexAttribute> ensureList(ComplexAttributeId key, Map<ComplexAttributeId, List<ComplexAttribute>> map) {
		return map.computeIfAbsent(key, x -> new ArrayList<>());
	}

	private static class PrimitiveCollector {
		private final PrimitiveAttributeId id;
		private final AttributeCondition condition;
		private double value;

		PrimitiveCollector(PrimitiveAttribute prototype) {
			this.id = prototype.getId();
			this.condition = prototype.getCondition();
		}

		void add(PrimitiveAttribute attribute) {
			this.value += attribute.getDouble();
		}

		void subtract(PrimitiveAttribute attribute) {
			this.value -= attribute.getDouble();
		}

		PrimitiveAttribute getResult() {
			return Attribute.ofNullable(id, value, condition);
		}
	}
}
