package wow.commons.model.spell;

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
	FRIEND_AOE(true),
	FRIENDS_PARTY(true),

	PARTY(true),
	PARTY_AOE(true),

	ENEMY(false),
	ENEMY_AOE(false),

	TARGET(false),
	ATTACKER(false)
	;

	private final boolean friendly;

	public static SpellTarget parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public boolean isHostile() {
		return !friendly;
	}
}
