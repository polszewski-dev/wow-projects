package wow.commons.model.buffs;

/**
 * User: POlszewski
 * Date: 2021-12-26
 */
public enum BuffExclusionGroup {
	BUFF,
	SELF_BUFF,
	OIL,
	FLASK,
	FOOD,
	POTION,
	RACIAL,

	ARMOR,
	DEMONIC_SACRIFICE,
	COE;

	public static BuffExclusionGroup parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return valueOf(value.toUpperCase());
	}
}
