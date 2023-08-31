package wow.commons.util;

import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User: POlszewski
 * Date: 2023-03-19
 */
public final class PrimitiveAttributeFormatter {
	private static final Properties PROPERTIES = new Properties();

	static {
		try (InputStream input = PrimitiveAttributeFormatter.class.getResourceAsStream("/attribute-formats.properties")) {
			PROPERTIES.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String format(PrimitiveAttribute attribute) {
		String formatStr = getFormatStr(attribute);

		return String.format(formatStr, getValueString(attribute));
	}

	private static String getFormatStr(PrimitiveAttribute attribute) {
		String key = attribute.id().getKey();
		AttributeCondition condition = attribute.condition();

		String formatStr = PROPERTIES.getProperty(key + "," + condition);

		if (formatStr != null) {
			return formatStr;
		}

		formatStr = PROPERTIES.getProperty(key);

		if (formatStr != null) {
			return formatStr + getConditionString(condition);
		}

		return "%s " + key + getConditionString(condition);
	}

	private static String getValueString(PrimitiveAttribute attribute) {
		return switch (attribute.id().getValueType()) {
			case POINT, RATING -> FormatUtil.decimalPointOnlyIfNecessary(attribute.getDouble());
			case PERCENT -> attribute.getPercent().toString();
			case DURATION -> attribute.getDuration().toString();
		};
	}

	public static String getConditionString(AttributeCondition condition) {
		return condition.isEmpty() ? "" : " | " + condition.getConditionString();
	}

	private PrimitiveAttributeFormatter() {}
}
