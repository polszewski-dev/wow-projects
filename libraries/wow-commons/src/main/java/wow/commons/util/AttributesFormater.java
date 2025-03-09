package wow.commons.util;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeScaling;
import wow.commons.model.attribute.Attributes;

import java.util.stream.Collectors;

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
		var withoutCondition = formatWithoutCondition(attribute);

		if (!attribute.hasCondition()) {
			return withoutCondition;
		}

		return "%s [%s]".formatted(withoutCondition, attribute.condition());
	}

	private static String formatWithoutCondition(Attribute attribute) {
		var valueStr = decimalPointOnlyIfNecessary(attribute.value(), "0.##");

		if (attribute.scaling() != AttributeScaling.NONE) {
			return "%s * %s %s".formatted(valueStr, attribute.scaling(), attribute.id());
		} else {
			return "%s %s".formatted(valueStr, attribute.id());
		}
	}

	private AttributesFormater() {}
}
