package wow.commons.model.attribute.condition;

import wow.commons.model.character.CharacterClassId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-27
 */
public record TargetClassCondition(CharacterClassId characterClassId) implements AttributeCondition {
	public TargetClassCondition {
		Objects.requireNonNull(characterClassId);
	}

	public static TargetClassCondition of(CharacterClassId characterClassId) {
		return new TargetClassCondition(characterClassId);
	}

	public static TargetClassCondition tryParse(String value) {
		if (value != null && value.startsWith(PREFIX)) {
			return of(CharacterClassId.parse(value.replace(PREFIX, "")));
		}
		return null;
	}

	@Override
	public String toString() {
		return PREFIX + characterClassId;
	}

	private static final String PREFIX = "TargetClass=";
}
