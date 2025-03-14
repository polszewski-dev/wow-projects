package wow.scraper.parser.spell.params;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-05
 */
public record AttributePattern(
		String id,
		String value
) {
	public AttributePattern {
		Objects.requireNonNull(id);
		Objects.requireNonNull(value);
	}
}
