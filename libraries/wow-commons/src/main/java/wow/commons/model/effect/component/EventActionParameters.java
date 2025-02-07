package wow.commons.model.effect.component;

import wow.commons.model.spell.AbilityId;

/**
 * User: POlszewski
 * Date: 2025-02-07
 */
public record EventActionParameters(
		Double value,
		AbilityId abilityId
) {
	public static final EventActionParameters EMPTY = new EventActionParameters(null, null);

	public static EventActionParameters parse(String string) {
		return EventActionParametersParser.parse(string);
	}
}

final class EventActionParametersParser {
	public static EventActionParameters parse(String string) {
		if (string == null || string.isEmpty()) {
			return EventActionParameters.EMPTY;
		}

		if (string.matches("^\\d+$")) {
			return new EventActionParameters(Double.valueOf(string), null);
		}

		var abilityId = AbilityId.tryParse(string);

		if (abilityId != null) {
			return new EventActionParameters(null, abilityId);
		}

		return parseMap(string);
	}

	private static EventActionParameters parseMap(String string) {
		Double value = null;
		AbilityId abilityId = null;

		for (var pair : string.split(",")) {
			var fields = pair.split("=");
			var left = fields[0].trim();
			var right = fields[1].trim();

			switch (left) {
				case "value" -> value = Double.valueOf(right);
				case "ability" -> abilityId = AbilityId.parse(right);
				default -> throw new IllegalArgumentException(left);
			}
		}

		return new EventActionParameters(value, abilityId);
	}

	private EventActionParametersParser() {}
}
