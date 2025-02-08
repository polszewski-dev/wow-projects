package wow.scraper.parser.spell.params;

import wow.commons.model.attribute.AttributeId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-05
 */
public record AttributePattern(
		AttributeId id,
		String value,
		String condition,
		String scaling
) {
	public AttributePattern {
		Objects.requireNonNull(id);
		Objects.requireNonNull(value);
	}
}
