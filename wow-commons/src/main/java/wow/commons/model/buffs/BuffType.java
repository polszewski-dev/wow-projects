package wow.commons.model.buffs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-12-26
 */
@AllArgsConstructor
@Getter
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

	public static BuffType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
