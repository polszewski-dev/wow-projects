package wow.commons.util;

import lombok.RequiredArgsConstructor;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.AttributeScaling;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;

import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2025-03-05
 */
@RequiredArgsConstructor
public class AttributesParser {
	private final String value;

	private String attrValueStr;
	private String conditionStr;
	private String idStr;
	private String scalingStr;

	/*
		Supported syntax:

		10 Power
		10 Power [Spell]
		10 * level Power [Spell]
		10 * level Power [Spell] + 10 Power [Spell]

	 */

	public static Attributes parse(String value) {
		if (value == null || value.isBlank()) {
			return Attributes.EMPTY;
		}

		var list = Stream.of(value.split("\\+"))
				.map(AttributesParser::new)
				.map(AttributesParser::parse)
				.toList();

		return Attributes.of(list);
	}

	private Attribute parse() {
		splitIntoParts();

		var id = AttributeId.parse(idStr);
		var attrValue = Double.parseDouble(attrValueStr);
		var condition = AttributeCondition.parse(conditionStr);
		var scaling = AttributeScaling.parse(scalingStr);

		if (scaling == null) {
			scaling = AttributeScaling.NONE;
		}

		return Attribute.of(id, attrValue, condition, scaling);
	}

	private void splitIntoParts() {
		var matcher = PATTERN.matcher(value.trim());

		if (!matcher.find()) {
			throw new IllegalArgumentException("Incorrect attribute format: " + value);
		}

		this.attrValueStr = matcher.group(1);
		this.idStr = matcher.group(4);
		this.scalingStr = matcher.group(3);
		this.conditionStr = matcher.group(6);
	}

	private static final Pattern PATTERN = Pattern.compile("^(-?\\d*\\.?\\d+)\\s*(\\*\\s*(.+)\\s+)?([\\w.%]+)\\s*(\\[(.+)])?$");
}
