package wow.character.model.script;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
public enum ScriptCommandTarget {
	DEFAULT,
	SELF,
	TARGET,
	FOCUS,
	MASTER;

	public static ScriptCommandTarget parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
