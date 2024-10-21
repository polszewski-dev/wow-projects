package wow.scraper.parser.spell.params;

import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.spell.Conversion;
import wow.commons.model.spell.SpellTarget;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-15
 */
public record ConversionParams(
		ConversionType type,
		SpellTarget target,
		AttributeCondition condition,
		Conversion.From from,
		Conversion.To to,
		String ratio
) {
	public ConversionParams {
		if (!(type == ConversionType.EFFECT && target != null) && !(type == ConversionType.SPELL && target == null)) {
			throw new IllegalArgumentException();
		}
		Objects.requireNonNull(condition);
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);
		Objects.requireNonNull(ratio);
	}
}
