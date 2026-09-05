package wow.commons.model.spell;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2026-09-05
 */
public enum PrimaryTargetType {
	SELF,
	PET,
	MASTER,
	FRIEND,
	ENEMY,
	ANY
	;

	public static PrimaryTargetType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
