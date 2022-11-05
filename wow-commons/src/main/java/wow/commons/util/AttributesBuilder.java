package wow.commons.util;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.*;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2021-10-16
 */
public class AttributesBuilder {
	private List<PrimitiveAttribute> attributeList;
	private Map<AttributeId, List<ComplexAttribute>> complexAttributeList;
	private final AttributeFilter filter;

	public AttributesBuilder() {
		this(null);
	}

	public AttributesBuilder(AttributeFilter filter) {
		this.filter = filter;
	}

	public Attributes toAttributes() {
		Attributes result = isEmpty() ? Attributes.EMPTY : Attributes.of(
				attributeList != null ? attributeList : List.of(),
				complexAttributeList != null ? complexAttributeList : Map.of()
		);
		this.attributeList = null;
		this.complexAttributeList = null;
		return result;
	}

	public boolean isEmpty() {
		return (attributeList == null || attributeList.isEmpty()) && (complexAttributeList == null || complexAttributeList.isEmpty());
	}

	public AttributesBuilder addAttribute(Attribute attribute) {
		if (attribute == null) {
			return this;
		}
		if (!attribute.isMatchedBy(filter)) {
			return this;
		}
		if (attribute instanceof PrimitiveAttribute) {
			getOrCreateAttributeList().add((PrimitiveAttribute)attribute);
		} else {
			getOrCreateComplexAttributeList().computeIfAbsent(attribute.getId(), x -> new ArrayList<>())
								.add((ComplexAttribute)attribute);
		}
		return this;
	}

	private List<PrimitiveAttribute> getOrCreateAttributeList() {
		if (attributeList == null) {
			attributeList = new ArrayList<>();
		}
		return attributeList;
	}

	private Map<AttributeId, List<ComplexAttribute>> getOrCreateComplexAttributeList() {
		if (complexAttributeList == null) {
			complexAttributeList = new EnumMap<>(AttributeId.class);
		}
		return complexAttributeList;
	}

	public AttributesBuilder addAttributeList(Collection<PrimitiveAttribute> attributes) {
		if (attributes != null) {
			if (filter != null) {
				for (Attribute attribute : attributes) {
					addAttribute(attribute);
				}
			} else {
				getOrCreateAttributeList().addAll(attributes);
			}
		}
		return this;
	}

	public AttributesBuilder addComplexAttributeList(Collection<ComplexAttribute> attributes) {
		if (attributes != null) {
			for (Attribute attribute : attributes) {
				addAttribute(attribute);
			}
		}
		return this;
	}

	public AttributesBuilder addAttribute(AttributeId attributeId, double value) {
		return addAttribute(attributeId, value, null);
	}

	public AttributesBuilder addAttribute(AttributeId attributeId, double value, AttributeCondition condition) {
		addAttribute(Attribute.ofNullable(attributeId, value, condition));
		return this;
	}

	public AttributesBuilder addAttribute(AttributeId attributeId, Percent value) {
		return addAttribute(attributeId, value, null);
	}

	public AttributesBuilder addAttribute(AttributeId attributeId, Percent value, AttributeCondition condition) {
		addAttribute(Attribute.ofNullable(attributeId, value, condition));
		return this;
	}

	public AttributesBuilder addAttribute(AttributeId attributeId, boolean value) {
		addAttribute(Attribute.ofNullable(attributeId, value));
		return this;
	}

	public AttributesBuilder addAttribute(AttributeId attributeId, boolean value, AttributeCondition condition) {
		addAttribute(Attribute.ofNullable(attributeId, value, condition));
		return this;
	}

	public AttributesBuilder addAttribute(AttributeId attributeId, Duration value) {
		addAttribute(Attribute.ofNullable(attributeId, value));
		return this;
	}

	public AttributesBuilder addAttribute(AttributeId attributeId, Duration value, AttributeCondition condition) {
		addAttribute(Attribute.ofNullable(attributeId, value, condition));
		return this;
	}

	public AttributesBuilder addAttributes(AttributeSource attributeSource) {
		if (attributeSource != null) {
			Attributes attributes = attributeSource.getAttributes();
			addAttributeList(attributes.getPrimitiveAttributeList());
			for (var entry : attributes.getComplexAttributeList().entrySet()) {
				addComplexAttributeList(entry.getValue());
			}
		}
		return this;
	}

	public AttributesBuilder addAttributes(Collection<? extends AttributeSource> attributeSources) {
		for (AttributeSource attributeSource : attributeSources) {
			addAttributes(attributeSource);
		}
		return this;
	}

	public AttributesBuilder removeAttribute(AttributeId attributeId) {
		if (attributeId.isComplexAttribute()) {
			if (complexAttributeList != null) {
				complexAttributeList.remove(attributeId);
			}
		} else {
			if (attributeList != null) {
				attributeList.removeIf(attribute -> attribute.getId() == attributeId);
			}
		}
		return this;
	}

	public AttributesBuilder removeAttributes(Collection<AttributeId> attributeIdList) {
		for (AttributeId attributeId : attributeIdList) {
			removeAttribute(attributeId);
		}
		return this;
	}

	public static Attributes list(Collection<? extends AttributeSource> attributeSources) {
		return new AttributesBuilder().addAttributes(attributeSources).toAttributes();
	}

	public static Attributes filter(Attributes attributes, AttributeFilter filter) {
		if (attributes.isEmpty()) {
			return Attributes.EMPTY;
		}

		return new AttributesBuilder(filter)
				.addAttributes(attributes)
				.toAttributes();
	}

	public static Attributes filter(AttributeSource attributeSource, SpellSchool spellSchool) {
		return filter(attributeSource.getAttributes(), AttributeFilter.of(spellSchool));
	}

	public static Attributes filter(AttributeSource attributeSource, SpellId spellId) {
		return filter(attributeSource.getAttributes(), AttributeFilter.of(spellId));
	}

	public static AttributesDiff diff(Attributes attributes1, Attributes attributes2) {
		Map<AttributeId, Map<AttributeCondition, Double>> doubleAttributes = new EnumMap<>(AttributeId.class);
		Map<AttributeId, Map<AttributeCondition, Percent>> percentAttributes = new EnumMap<>(AttributeId.class);
		Map<AttributeId, Map<AttributeCondition, Boolean>> booleanAttributes = new EnumMap<>(AttributeId.class);
		Map<AttributeId, Map<AttributeCondition, Duration>> durationAttributes = new EnumMap<>(AttributeId.class);

		for (PrimitiveAttribute attribute : attributes1.getPrimitiveAttributeList()) {
			AttributeId id = attribute.getId();
			if (id.isDoubleAttribute()) {
				var byCond = doubleAttributes.computeIfAbsent(id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), 0.0) + attribute.getDouble());
			} else if (id.isPercentAttribute()) {
				var byCond = percentAttributes.computeIfAbsent(id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), Percent.ZERO).add(attribute.getPercent()));
			} else if (id.isBooleanAttribute()) {
				var byCond = booleanAttributes.computeIfAbsent(id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), false) || attribute.getBoolean());
			} else if (id.isDurationAttribute()) {
				var byCond = durationAttributes.computeIfAbsent(id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), Duration.ZERO).add(attribute.getDuration()));
			} else {
				throw new IllegalArgumentException(attribute.toString());
			}
		}

		for (PrimitiveAttribute attribute : attributes2.getPrimitiveAttributeList()) {
			AttributeId id = attribute.getId();
			if (id.isDoubleAttribute()) {
				var byCond = doubleAttributes.computeIfAbsent(id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), 0.0) - attribute.getDouble());
			} else if (id.isPercentAttribute()) {
				var byCond = percentAttributes.computeIfAbsent(id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), Percent.ZERO).subtract(attribute.getPercent()));
			} else if (id.isBooleanAttribute()) {
				var byCond = booleanAttributes.computeIfAbsent(id, x -> new LinkedHashMap<>());
				Boolean value2 = byCond.getOrDefault(attribute.getCondition(), false);
				boolean value1 = attribute.getBoolean();
				if (value1 != value2) {
					throw new IllegalArgumentException("Boolean attribute: " + id + " has different values");
				}
			} else if (id.isDurationAttribute()) {
				var byCond = durationAttributes.computeIfAbsent(id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), Duration.ZERO).subtract(attribute.getDuration()));
			} else {
				throw new IllegalArgumentException(attribute.toString());
			}
		}

		AttributesDiff result = new AttributesDiff();
		AttributesBuilder builder = new AttributesBuilder();

		List<PrimitiveAttribute> attributes = new ArrayList<>();

		for (var entry : doubleAttributes.entrySet()) {
			for (var entry2 : entry.getValue().entrySet()) {
				PrimitiveAttribute attribute = Attribute.ofNullable(entry.getKey(), entry2.getValue(), entry2.getKey());
				if (attribute != null) {
					attributes.add(attribute);
				}
			}
		}

		for (var entry : percentAttributes.entrySet()) {
			for (var entry2 : entry.getValue().entrySet()) {
				PrimitiveAttribute attribute = Attribute.ofNullable(entry.getKey(), entry2.getValue(), entry2.getKey());
				if (attribute != null) {
					attributes.add(attribute);
				}
			}
		}

		for (var entry : booleanAttributes.entrySet()) {
			for (var entry2 : entry.getValue().entrySet()) {
				PrimitiveAttribute attribute = Attribute.ofNullable(entry.getKey(), entry2.getValue(), entry2.getKey());
				if (attribute != null) {
					attributes.add(attribute);
				}
			}
		}

		for (var entry : durationAttributes.entrySet()) {
			for (var entry2 : entry.getValue().entrySet()) {
				PrimitiveAttribute attribute = Attribute.ofNullable(entry.getKey(), entry2.getValue(), entry2.getKey());
				if (attribute != null) {
					attributes.add(attribute);
				}
			}
		}

		attributes.sort(Comparator.comparing(Attribute::getId));
		builder.addAttributeList(attributes);

		result.attributes = builder.toAttributes();

		for (var entry : attributes1.getComplexAttributeList().entrySet()) {
			result.getAddedAbilities().computeIfAbsent(entry.getKey(), x -> new ArrayList<>()).addAll(entry.getValue());
		}

		for (var entry : attributes2.getComplexAttributeList().entrySet()) {
			result.getAddedAbilities().computeIfAbsent(entry.getKey(), x -> new ArrayList<>()).removeAll(entry.getValue());
		}

		for (var entry : attributes2.getComplexAttributeList().entrySet()) {
			result.getRemovedAbilities().computeIfAbsent(entry.getKey(), x -> new ArrayList<>()).addAll(entry.getValue());
		}

		for (var entry : attributes1.getComplexAttributeList().entrySet()) {
			result.getRemovedAbilities().computeIfAbsent(entry.getKey(), x -> new ArrayList<>()).removeAll(entry.getValue());
		}

		return result;
	}
}
