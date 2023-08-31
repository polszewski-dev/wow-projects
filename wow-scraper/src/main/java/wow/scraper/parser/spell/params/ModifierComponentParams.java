package wow.scraper.parser.spell.params;

import wow.commons.model.spell.SpellTarget;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-09
 */
public record ModifierComponentParams(
		SpellTarget target,
		List<AttributePattern> attributes
) {
	public ModifierComponentParams {
		Objects.requireNonNull(target);
		Objects.requireNonNull(attributes);
	}
}
