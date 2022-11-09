package wow.commons.util;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.*;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.primitive.*;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2021-10-16
 */
public class AttributesBuilder {
	private List<PrimitiveAttribute> attributeList;
	private Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeList;
	private final AttributeFilter filter;

	public AttributesBuilder() {
		this(null);
	}

	public AttributesBuilder(AttributeFilter filter) {
		this.filter = filter;
	}

	public Attributes toAttributes() {
		Attributes result = getAttributes();
		this.attributeList = null;
		this.complexAttributeList = null;
		return result;
	}

	private Attributes getAttributes() {
		if (isEmpty()) {
			return Attributes.EMPTY;
		}

		return Attributes.of(
				attributeList != null ? attributeList : List.of(),
				complexAttributeList != null ? complexAttributeList : Map.of()
		);
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
		if (attribute.getId().isPrimitiveAttribute()) {
			getOrCreateAttributeList().add((PrimitiveAttribute)attribute);
		} else {
			getOrCreateComplexAttributeList().computeIfAbsent((ComplexAttributeId) attribute.getId(), x -> new ArrayList<>())
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

	private Map<ComplexAttributeId, List<ComplexAttribute>> getOrCreateComplexAttributeList() {
		if (complexAttributeList == null) {
			complexAttributeList = new EnumMap<>(ComplexAttributeId.class);
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

	public AttributesBuilder addAttribute(DoubleAttributeId attributeId, double value) {
		return addAttribute(attributeId, value, null);
	}

	public AttributesBuilder addAttribute(DoubleAttributeId attributeId, double value, AttributeCondition condition) {
		addAttribute(Attribute.ofNullable(attributeId, value, condition));
		return this;
	}

	public AttributesBuilder addAttribute(PercentAttributeId attributeId, Percent value) {
		return addAttribute(attributeId, value, null);
	}

	public AttributesBuilder addAttribute(PercentAttributeId attributeId, Percent value, AttributeCondition condition) {
		addAttribute(Attribute.ofNullable(attributeId, value, condition));
		return this;
	}

	public AttributesBuilder addAttribute(BooleanAttributeId attributeId, boolean value) {
		addAttribute(Attribute.ofNullable(attributeId, value));
		return this;
	}

	public AttributesBuilder addAttribute(BooleanAttributeId attributeId, boolean value, AttributeCondition condition) {
		addAttribute(Attribute.ofNullable(attributeId, value, condition));
		return this;
	}

	public AttributesBuilder addAttribute(DurationAttributeId attributeId, Duration value) {
		addAttribute(Attribute.ofNullable(attributeId, value));
		return this;
	}

	public AttributesBuilder addAttribute(DurationAttributeId attributeId, Duration value, AttributeCondition condition) {
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
		var doubleAttributes = new EnumMap<DoubleAttributeId, Map<AttributeCondition, Double>>(DoubleAttributeId.class);
		var percentAttributes = new EnumMap<PercentAttributeId, Map<AttributeCondition, Percent>>(PercentAttributeId.class);
		var booleanAttributes = new EnumMap<BooleanAttributeId, Map<AttributeCondition, Boolean>>(BooleanAttributeId.class);
		var durationAttributes = new EnumMap<DurationAttributeId, Map<AttributeCondition, Duration>>(DurationAttributeId.class);

		for (var attribute : attributes1.getPrimitiveAttributeList()) {
			AttributeId id = attribute.getId();
			if (id.isDoubleAttribute()) {
				var byCond = doubleAttributes.computeIfAbsent((DoubleAttributeId) id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), 0.0) + attribute.getDouble());
			} else if (id.isPercentAttribute()) {
				var byCond = percentAttributes.computeIfAbsent((PercentAttributeId) id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), Percent.ZERO).add(attribute.getPercent()));
			} else if (id.isBooleanAttribute()) {
				var byCond = booleanAttributes.computeIfAbsent((BooleanAttributeId) id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), false) || attribute.getBoolean());
			} else if (id.isDurationAttribute()) {
				var byCond = durationAttributes.computeIfAbsent((DurationAttributeId) id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), Duration.ZERO).add(attribute.getDuration()));
			} else {
				throw new IllegalArgumentException(attribute.toString());
			}
		}

		for (var attribute : attributes2.getPrimitiveAttributeList()) {
			AttributeId id = attribute.getId();
			if (id.isDoubleAttribute()) {
				var byCond = doubleAttributes.computeIfAbsent((DoubleAttributeId) id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), 0.0) - attribute.getDouble());
			} else if (id.isPercentAttribute()) {
				var byCond = percentAttributes.computeIfAbsent((PercentAttributeId) id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), Percent.ZERO).subtract(attribute.getPercent()));
			} else if (id.isBooleanAttribute()) {
				var byCond = booleanAttributes.computeIfAbsent((BooleanAttributeId) id, x -> new LinkedHashMap<>());
				Boolean value2 = byCond.getOrDefault(attribute.getCondition(), false);
				boolean value1 = attribute.getBoolean();
				if (value1 != value2) {
					throw new IllegalArgumentException("Boolean attribute: " + id + " has different values");
				}
			} else if (id.isDurationAttribute()) {
				var byCond = durationAttributes.computeIfAbsent((DurationAttributeId) id, x -> new LinkedHashMap<>());
				byCond.put(attribute.getCondition(), byCond.getOrDefault(attribute.getCondition(), Duration.ZERO).subtract(attribute.getDuration()));
			} else {
				throw new IllegalArgumentException(attribute.toString());
			}
		}

		AttributesDiff result = new AttributesDiff();

		List<PrimitiveAttribute> attributes = new ArrayList<>();

		for (var entry : doubleAttributes.entrySet()) {
			for (var entry2 : entry.getValue().entrySet()) {
				var attribute = Attribute.ofNullable(entry.getKey(), entry2.getValue(), entry2.getKey());
				if (attribute != null) {
					attributes.add(attribute);
				}
			}
		}

		for (var entry : percentAttributes.entrySet()) {
			for (var entry2 : entry.getValue().entrySet()) {
				var attribute = Attribute.ofNullable(entry.getKey(), entry2.getValue(), entry2.getKey());
				if (attribute != null) {
					attributes.add(attribute);
				}
			}
		}

		for (var entry : booleanAttributes.entrySet()) {
			for (var entry2 : entry.getValue().entrySet()) {
				var attribute = Attribute.ofNullable(entry.getKey(), entry2.getValue(), entry2.getKey());
				if (attribute != null) {
					attributes.add(attribute);
				}
			}
		}

		for (var entry : durationAttributes.entrySet()) {
			for (var entry2 : entry.getValue().entrySet()) {
				var attribute = Attribute.ofNullable(entry.getKey(), entry2.getValue(), entry2.getKey());
				if (attribute != null) {
					attributes.add(attribute);
				}
			}
		}

		attributes.sort(Comparator.comparingInt(x -> x.getId().getSortOrder()));

		result.attributes = new AttributesBuilder()
				.addAttributeList(attributes)
				.toAttributes();

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
