package wow.commons.util;

import lombok.RequiredArgsConstructor;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.AttributeScaling;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.talent.TalentTree;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.Double.parseDouble;
import static wow.commons.model.attribute.AttributeScaling.*;
import static wow.commons.util.AttributesFormater.ATTRIBUTE_SEPARATOR;

/**
 * User: POlszewski
 * Date: 2025-03-05
 */
@RequiredArgsConstructor
public class AttributesParser {
	private final String value;

	private String scaledValueStr;
	private String conditionStr;
	private String idStr;
	private double attributeValue;
	private AttributeScaling attributeScaling;

	/*
		Supported syntax:

		10 Power
		10 Power [Spell]
		10 * level Power [Spell]
		10 + 20 * level Power [Spell]
		10 * numEffectsOnTarget(Affliction, 60) Power [Spell]
		10 * level Power [Spell] ; 10 Power [Spell]

	 */

	public static Attributes parse(String value) {
		if (value == null || value.isBlank()) {
			return Attributes.EMPTY;
		}

		var list = Stream.of(value.split(Pattern.quote(ATTRIBUTE_SEPARATOR)))
				.map(AttributesParser::new)
				.map(AttributesParser::parse)
				.toList();

		return Attributes.of(list);
	}

	public static Attribute parse(String id, double value) {
		return parse(value + " " + id).list().getFirst();
	}

	private Attribute parse() {
		splitIntoParts();
		parseScaledValue();

		var id = AttributeId.parse(idStr);
		var condition = AttributeCondition.parse(conditionStr);

		return Attribute.of(id, attributeValue, condition, attributeScaling);
	}

	private void splitIntoParts() {
		var matcher = PATTERN.matcher(value.trim());

		if (!matcher.find()) {
			throw new IllegalArgumentException("Incorrect attribute format: " + value);
		}

		this.scaledValueStr = matcher.group(1);
		this.idStr = matcher.group(2);
		this.conditionStr = matcher.group(4);
	}

	private static final String SCALED_VALUE_REGEX =
			"{value}|" +
			"{value}\\s*\\*\\s*level|" +
			"{value}\\s*\\+\\s*{value}\\s*\\*\\s*level|" +
			"{value}\\s*\\*\\s*numEffectsOnTarget\\({tree},\\s*{value}\\)";
	private static final String VALUE_REGEX = "-?\\d*\\.?\\d+";
	private static final String TREE_REGEX = "\\w+";

	private static final Pattern PATTERN = Pattern.compile(
			"^({scaledValue})\\s+({id})\\s*(\\[({condition})])?$"
					.replace("{scaledValue}", SCALED_VALUE_REGEX)
					.replace("{id}", "[\\w.%]+")
					.replace("{condition}", ".+")
					.replace("{tree}", TREE_REGEX)
					.replace("{value}", VALUE_REGEX)
	);

	private static final Pattern SCALED_VALUE_PATTERN = Pattern.compile(
			("^(" + SCALED_VALUE_REGEX + ")$")
					.replace("{value}", "(" + VALUE_REGEX + ")")
					.replace("{tree}", "(" + TREE_REGEX + ")")
	);

	private void parseScaledValue() {
		var matcher = SCALED_VALUE_PATTERN.matcher(scaledValueStr);

		if (!matcher.find()) {
			throw new IllegalArgumentException("Incorrect scaled value format: " + scaledValueStr);
		}

		if (matcher.group(2) != null) {
			attributeValue = parseDouble(matcher.group(2));
			attributeScaling = NONE;
		} else if (matcher.group(3) != null) {
			attributeValue = parseDouble(matcher.group(3));
			attributeScaling = LEVEL;
		} else if (matcher.group(4) != null) {
			attributeValue = parseDouble(matcher.group(4));
			var factor = parseDouble(matcher.group(5));
			attributeScaling = new LevelScalingByFactor(factor);
		} else if (matcher.group(6) != null) {
			attributeValue = parseDouble(matcher.group(6));
			var treeStr = matcher.group(7).trim();
			var maxStr = matcher.group(8).trim();
			attributeScaling = new NumberOfEffectsOnTarget(TalentTree.parse(treeStr), Percent.parse(maxStr));
		}
	}
}
