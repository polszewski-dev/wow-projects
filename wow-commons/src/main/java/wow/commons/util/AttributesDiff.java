package wow.commons.util;

import lombok.Getter;
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
@Getter
public class AttributesDiff {
	private Attributes attributes;
	private final Map<ComplexAttributeId, List<ComplexAttribute>> addedAbilities = new EnumMap<>(ComplexAttributeId.class);
	private final Map<ComplexAttributeId, List<ComplexAttribute>> removedAbilities = new EnumMap<>(ComplexAttributeId.class);

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}
}
