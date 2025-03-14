package wow.commons.util;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeScaling;
import wow.commons.model.attribute.Attributes;

import java.util.stream.Collectors;

import static wow.commons.model.attribute.AttributeScaling.*;
import static wow.commons.util.FormatUtil.decimalPointOnlyIfNecessary;

/**
 * User: POlszewski
 * Date: 2025-03-05
 */
public class AttributesFormater {
	public static String format(Attributes attributes) {
		return attributes.list().stream()
				.map(AttributesFormater::format)
				.collect(Collectors.joining(" + "));
	}

	private static String format(Attribute attribute) {
		var valueStr = decimalPointOnlyIfNecessary(attribute.value(), "0.##");
		var idStr = formatWithoutValue(attribute);

		return valueStr + " " + idStr;
	}

	public static String formatWithoutValue(Attribute attribute) {
		var withoutCondition = formatWithoutCondition(attribute);

		if (!attribute.hasCondition()) {
			return withoutCondition;
		}

		return "%s [%s]".formatted(withoutCondition, attribute.condition());
	}

	private static String formatWithoutCondition(Attribute attribute) {
		if (attribute.scaling() != NONE) {
			return "* %s %s".formatted(formatAttributeScaling(attribute.scaling()), attribute.id());
		} else {
			return attribute.id().toString();
		}
	}

	private static String formatAttributeScaling(AttributeScaling scaling) {
		return switch (scaling) {
			case NoScaling() -> "none";
			case LevelScaling() -> "level";
			case NumberOfEffectsOnTarget(var tree, var max) ->
					"numEffectsOnTarget(%s, %s)".formatted(tree, decimalPointOnlyIfNecessary(max.value()));
		};
	}

	private AttributesFormater() {}
}
