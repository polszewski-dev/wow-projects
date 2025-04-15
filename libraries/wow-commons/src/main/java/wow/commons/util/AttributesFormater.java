package wow.commons.util;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeScaling;
import wow.commons.model.attribute.Attributes;

import static java.util.stream.Collectors.joining;
import static wow.commons.model.attribute.AttributeScaling.*;
import static wow.commons.util.FormatUtil.decimalPointOnlyIfNecessary;

/**
 * User: POlszewski
 * Date: 2025-03-05
 */
public class AttributesFormater {
	static final String ATTRIBUTE_SEPARATOR = "+";
	private static final String VALUE_PLACEHOLDER = "{value}";

	public static String format(Attributes attributes) {
		return attributes.list().stream()
				.map(AttributesFormater::format)
				.collect(joining(" " + ATTRIBUTE_SEPARATOR + " "));
	}

	private static String format(Attribute attribute) {
		var valueStr = valToStr(attribute.value());
		var withoutValue = formatWithPlaceholder(attribute);

		return withoutValue.replace(VALUE_PLACEHOLDER, valueStr);
	}

	public static String formatWithoutValue(Attribute attribute) {
		var withPlaceholder = formatWithPlaceholder(attribute);

		if (withPlaceholder.startsWith(VALUE_PLACEHOLDER)) {
			return withPlaceholder.substring(VALUE_PLACEHOLDER.length() + 1); // +1 for space
		}

		return withPlaceholder;
	}

	private static String formatWithPlaceholder(Attribute attribute) {
		var id = attribute.id();
		var scaledValue = getScaledValue(attribute.scaling());

		if (attribute.hasCondition()) {
			return "%s %s [%s]".formatted(scaledValue, id, attribute.condition());
		} else {
			return "%s %s".formatted(scaledValue, id);
		}
	}

	private static String getScaledValue(AttributeScaling scaling) {
		return switch (scaling) {
			case NoScaling() ->
					"%s".formatted(VALUE_PLACEHOLDER);
			case LevelScaling() ->
					"%s * level".formatted(VALUE_PLACEHOLDER);
			case NumberOfEffectsOnTarget(var tree, var max) ->
					"%s * numEffectsOnTarget(%s, %s)".formatted(VALUE_PLACEHOLDER, tree, valToStr(max.value()));
		};
	}

	private static String valToStr(double value) {
		return decimalPointOnlyIfNecessary(value, "0.##");
	}

	private AttributesFormater() {}
}
