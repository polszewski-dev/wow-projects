package wow.minmax.util;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.util.FormatUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * User: POlszewski
 * Date: 2023-03-19
 */
public final class AttributeFormatter {
	private static final Properties PROPERTIES = new Properties();

	static {
		try (var input = AttributeFormatter.class.getResourceAsStream("/attribute-formats.properties")) {
			PROPERTIES.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String format(PrimitiveAttribute attribute) {
		var formatStr = getFormatStr(attribute);

		if (formatStr != null) {
			return String.format(formatStr, getValueString(attribute));
		} else {
			return attribute.toString();
		}
	}

	private static String getFormatStr(PrimitiveAttribute attribute) {
		var key = attribute.id().getKey();
		var condition = attribute.condition();
		var propertyKey = condition.isEmpty() ? key : key + "," + condition.toString().replace(" ", "");

		return PROPERTIES.getProperty(propertyKey);
	}

	private static String getValueString(PrimitiveAttribute attribute) {
		return switch (attribute.id().getValueType()) {
			case POINT, RATING -> FormatUtil.decimalPointOnlyIfNecessary(attribute.value());
			case PERCENT -> Percent.of(attribute.value()).toString();
			case DURATION -> Duration.seconds(attribute.value()).toString();
		};
	}

	private AttributeFormatter() {}
}
