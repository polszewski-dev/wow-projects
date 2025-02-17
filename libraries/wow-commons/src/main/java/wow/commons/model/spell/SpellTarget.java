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
	SELF(true, true),
	PET(true, true),

	FRIEND(true, true),
	FRIEND_AOE(true, false),
	FRIENDS_PARTY(true, true),

	PARTY(true, false),
	PARTY_AOE(true, false),

	ENEMY(false, true),
	ENEMY_AOE(false, false),

	TARGET(false, true),
	ATTACKER(false, true),

	GROUND_HOSTILE(false, false)
	;

	private final boolean friendly;
	private final boolean single;

	public static SpellTarget parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public boolean isHostile() {
		return !friendly;
	}

	public boolean isAoE() {
		return !single;
	}
}
