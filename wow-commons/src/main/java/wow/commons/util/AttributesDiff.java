package wow.commons.util;

import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.ComplexAttribute;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-10-13
 */
public class AttributesDiff {
	Attributes attributes;
	final Map<AttributeId, List<ComplexAttribute>> addedAbilities = new EnumMap<>(AttributeId.class);
	final Map<AttributeId, List<ComplexAttribute>> removedAbilities = new EnumMap<>(AttributeId.class);

	public Attributes getAttributes() {
		return attributes;
	}

	public Map<AttributeId, List<ComplexAttribute>> getAddedAbilities() {
		return addedAbilities;
	}

	public Map<AttributeId, List<ComplexAttribute>> getRemovedAbilities() {
		return removedAbilities;
	}

	@Override
	public String toString() {
		String added = addedAbilities.entrySet()
				.stream()
				.filter(e -> !e.getValue().isEmpty())
				.map(e -> String.format("++%s=%s", e.getKey(), e.getValue()))
				.collect(Collectors.joining(", "));
		String removed = removedAbilities.entrySet()
				.stream()
				.filter(e -> !e.getValue().isEmpty())
				.map(e -> String.format("--%s=%s", e.getKey(), e.getValue()))
				.collect(Collectors.joining(", "));

		return String.join(", ", attributes.toString(), added, removed);
	}
}
