package wow.commons.model.profession;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-07-31
 */
public enum ProfessionProficiencyId {
	APPRENTICE,
	JOURNEYMAN,
	EXPERT,
	ARTISAN,
	MASTER,
	GRAND_MASTER;

	public static ProfessionProficiencyId parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
