package wow.commons.util;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-10-13
 */
public class AttributesDiff {
	private Attributes attributes;
	private final Map<ComplexAttributeId, List<ComplexAttribute>> addedAbilities = new EnumMap<>(ComplexAttributeId.class);
	private final Map<ComplexAttributeId, List<ComplexAttribute>> removedAbilities = new EnumMap<>(ComplexAttributeId.class);

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public Map<ComplexAttributeId, List<ComplexAttribute>> getAddedAbilities() {
		return addedAbilities;
	}

	public Map<ComplexAttributeId, List<ComplexAttribute>> getRemovedAbilities() {
		return removedAbilities;
	}
}
