package wow.commons.model.buffs;

/**
 * User: POlszewski
 * Date: 2021-12-26
 */
public enum BuffType {
	BUFF,
	SELF_BUFF,
	DEBUFF,
	OIL(BuffExclusionGroup.OIL),
	FLASK(BuffExclusionGroup.FLASK),
	FOOD(BuffExclusionGroup.FOOD),
	POTION(BuffExclusionGroup.POTION),
	RACIAL;

	private final BuffExclusionGroup defaultExclusionGroup;

	BuffType() {
		this(null);
	}

	BuffType(BuffExclusionGroup defaultExclusionGroup) {
		this.defaultExclusionGroup = defaultExclusionGroup;
	}

	public BuffExclusionGroup getDefaultExclusionGroup() {
		return defaultExclusionGroup;
	}
}
