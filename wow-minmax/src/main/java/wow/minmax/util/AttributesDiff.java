package wow.minmax.util;

import lombok.Getter;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.complex.ComplexAttribute;
import wow.commons.model.attribute.complex.ComplexAttributeId;

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
