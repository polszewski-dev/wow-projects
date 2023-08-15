package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-08-15
 */
@AllArgsConstructor
@Getter
public enum SpellTarget {
	SELF(true),
	PET(true),
	FRIEND(true),
	FRIENDS_IN_AREA(true),
	ENEMY(false),
	ENEMIES_IN_AREA(false);

	private final boolean friendly;

	public static SpellTarget parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public boolean isHostile() {
		return !friendly;
	}
}
