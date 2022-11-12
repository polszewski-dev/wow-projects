package wow.commons.util;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2021-10-16
 */
public class AttributesBuilder {
	private List<PrimitiveAttribute> attributeList;
	private Map<ComplexAttributeId, List<ComplexAttribute>> complexAttributeList;

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

	public AttributesBuilder addAttribute(PrimitiveAttribute attribute) {
		if (attribute != null) {
			getOrCreateAttributeList().add(attribute);
		}
		return this;
	}

	public AttributesBuilder addAttribute(ComplexAttribute attribute) {
		if (attribute != null) {
			getOrCreateComplexAttributeList()
					.computeIfAbsent(attribute.getId(), x -> new ArrayList<>())
					.add(attribute);
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
			getOrCreateAttributeList().addAll(attributes);
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
}
