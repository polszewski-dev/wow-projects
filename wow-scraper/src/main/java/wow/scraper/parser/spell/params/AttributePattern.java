package wow.scraper.parser.spell.params;

import wow.commons.model.attribute.primitive.PrimitiveAttributeId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-05
 */
public record AttributePattern(
		PrimitiveAttributeId id,
		String value,
		String condition,
		boolean levelScaled
) {
	public AttributePattern {
		Objects.requireNonNull(id);
		Objects.requireNonNull(value);
	}
}
