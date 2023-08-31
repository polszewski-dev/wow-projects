package wow.commons.util;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeSource;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.complex.ComplexAttribute;
import wow.commons.model.attribute.complex.ComplexAttributeId;
import wow.commons.model.attribute.complex.special.SpecialAbility;
import wow.commons.model.attribute.complex.special.SpecialAbilitySource;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2021-10-16
 */
public class AttributesBuilder {
	private List<PrimitiveAttribute> primitiveAttributes;
	private Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeMap;

	public Attributes toAttributes() {
		Attributes result = getAttributes();
		this.primitiveAttributes = null;
		this.complexAttributeMap = null;
		return result;
	}

	private Attributes getAttributes() {
		if (isEmpty()) {
			return Attributes.EMPTY;
		}

		return Attributes.of(
				primitiveAttributes != null ? primitiveAttributes : List.of(),
				complexAttributeMap != null ? complexAttributeMap : Map.of()
		);
	}

	public boolean isEmpty() {
		return (primitiveAttributes == null || primitiveAttributes.isEmpty()) && (complexAttributeMap == null || complexAttributeMap.isEmpty());
	}

	public AttributesBuilder addAttribute(PrimitiveAttribute attribute) {
		if (attribute != null) {
			getOrCreateAttributeList().add(attribute);
		}
		return this;
	}

	public AttributesBuilder addAttribute(ComplexAttribute attribute) {
		if (attribute != null) {
			getOrCreateComplexAttributeMap()
					.computeIfAbsent(attribute.id(), x -> new ArrayList<>())
					.add(attribute);
		}
		return this;
	}

	private List<PrimitiveAttribute> getOrCreateAttributeList() {
		if (primitiveAttributes == null) {
			primitiveAttributes = new ArrayList<>();
		}
		return primitiveAttributes;
	}

	private Map<ComplexAttributeId, List<ComplexAttribute>> getOrCreateComplexAttributeMap() {
		if (complexAttributeMap == null) {
			complexAttributeMap = new EnumMap<>(ComplexAttributeId.class);
		}
		return complexAttributeMap;
	}

	public AttributesBuilder addAttributeList(Collection<PrimitiveAttribute> attributes) {
		if (attributes != null) {
			for (PrimitiveAttribute attribute : attributes) {
				addAttribute(attribute);
			}
		}
		return this;
	}

	public AttributesBuilder addComplexAttributeList(Collection<? extends ComplexAttribute> attributes) {
		if (attributes != null) {
			for (ComplexAttribute attribute : attributes) {
				addAttribute(attribute);
			}
		}
		return this;
	}

	public AttributesBuilder addAttribute(PrimitiveAttributeId attributeId, double value) {
		addAttribute(Attribute.ofNullable(attributeId, value));
		return this;
	}

	public AttributesBuilder addAttribute(PrimitiveAttributeId attributeId, double value, AttributeCondition condition) {
		addAttribute(Attribute.ofNullable(attributeId, value, condition));
		return this;
	}

	public AttributesBuilder addAttribute(PrimitiveAttributeId attributeId, Percent value) {
		addAttribute(Attribute.ofNullable(attributeId, value));
		return this;
	}

	public AttributesBuilder addAttribute(PrimitiveAttributeId attributeId, Percent value, AttributeCondition condition) {
		addAttribute(Attribute.ofNullable(attributeId, value, condition));
		return this;
	}

	public AttributesBuilder addAttribute(PrimitiveAttributeId attributeId, Duration value) {
		addAttribute(Attribute.ofNullable(attributeId, value));
		return this;
	}

	public AttributesBuilder addAttribute(PrimitiveAttributeId attributeId, Duration value, AttributeCondition condition) {
		addAttribute(Attribute.ofNullable(attributeId, value, condition));
		return this;
	}

	public AttributesBuilder addAttributes(AttributeSource attributeSource) {
		if (attributeSource != null) {
			Attributes attributes = attributeSource.getAttributes();
			addAttributeList(attributes.getPrimitiveAttributes());
			for (var entry : attributes.getComplexAttributeMap().entrySet()) {
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

	public AttributesBuilder addAttributes(Attributes attributes, AttributeCondition condition) {
		if (condition.isEmpty()) {
			addAttributes(attributes);
			return this;
		}

		for (PrimitiveAttribute attribute : attributes.getPrimitiveAttributes()) {
			addAttribute(attribute.attachCondition(condition));
		}

		for (List<ComplexAttribute> complexAttributes : attributes.getComplexAttributeMap().values()) {
			for (ComplexAttribute attribute : complexAttributes) {
				addAttribute(attribute.attachCondition(condition));
			}
		}

		return this;
	}

	public static Attributes attachCondition(Attributes attributes, AttributeCondition condition) {
		return new AttributesBuilder()
				.addAttributes(attributes, condition)
				.toAttributes();
	}

	public static Attributes attachSource(Attributes attributes, SpecialAbilitySource source) {
		var complexAttributes = attributes.getComplexAttributes().stream()
				.map(x -> attachSource(x, source))
				.toList();

		return new AttributesBuilder()
				.addAttributeList(attributes.getPrimitiveAttributes())
				.addComplexAttributeList(complexAttributes)
				.toAttributes();
	}

	private static ComplexAttribute attachSource(ComplexAttribute attribute, SpecialAbilitySource source) {
		if (attribute instanceof SpecialAbility specialAbility) {
			return specialAbility.attachSource(source);
		}
		return attribute;
	}

	public static Attributes list(Collection<? extends AttributeSource> attributeSources) {
		return new AttributesBuilder().addAttributes(attributeSources).toAttributes();
	}

	public static Attributes addAttribute(Attributes attributes, PrimitiveAttribute attribute) {
		return new AttributesBuilder()
				.addAttributes(attributes)
				.addAttribute(attribute)
				.toAttributes();
	}

	public static Attributes addAttributes(Attributes attributes1, Attributes attributes2) {
		return new AttributesBuilder()
				.addAttributes(attributes1)
				.addAttributes(attributes2)
				.toAttributes();
	}

	public static Attributes removeAttributes(Attributes attributes, Attributes attributesToRemove) {
		return Attributes.of(
				removePrimitiveAttributes(attributes, attributesToRemove),
				removeComplexAttributes(attributes, attributesToRemove)
		);
	}

	private static List<PrimitiveAttribute> removePrimitiveAttributes(Attributes attributes, Attributes attributesToRemove) {
		var result = new ArrayList<>(attributes.getPrimitiveAttributes());

		for (var attributeToRemove : attributesToRemove.getPrimitiveAttributes()) {
			result.add(attributeToRemove.negate());
		}

		return result;
	}

	private static Map<ComplexAttributeId, List<ComplexAttribute>> removeComplexAttributes(Attributes attributes, Attributes attributesToRemove) {
		if (attributesToRemove.isEmpty()) {
			return attributes.getComplexAttributeMap();
		}

		var result = new EnumMap<ComplexAttributeId, List<ComplexAttribute>>(ComplexAttributeId.class);
		result.putAll(attributes.getComplexAttributeMap());

		for (var entry : result.entrySet()) {
			entry.setValue(new ArrayList<>(entry.getValue()));
		}

		for (var entry : attributesToRemove.getComplexAttributeMap().entrySet()) {
			for (var complexAttributeToRemove : entry.getValue()) {
				boolean removed = result.get(entry.getKey())
						.remove(complexAttributeToRemove);

				if (!removed) {
					throw new IllegalArgumentException("Can't remove complex attribute that there is not on the list: " + complexAttributeToRemove);
				}
			}
		}

		return result;
	}
}
