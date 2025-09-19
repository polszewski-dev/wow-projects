package wow.character.model.script;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
@RequiredArgsConstructor
@Getter
public enum ScriptSectionType {
	ROTATION("Rotation"),
	PREPARATION("Preparation"),
	WARM_UP("WarmUp"),
	CAN_NOT_PAY_MANA_COST("CanNotPayManaCost"),
	CAN_NOT_PAY_HEALTH_COST("CanNotPayHealthCost");

	private final String name;

	public static ScriptSectionType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}
}
