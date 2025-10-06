package wow.commons.model.spell;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-10-23
 */
@AllArgsConstructor
@Getter
public enum ActionType {
	PHYSICAL("Physical"),
	SPELL("Spell");

	private final String name;

	public static ActionType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static ActionType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
