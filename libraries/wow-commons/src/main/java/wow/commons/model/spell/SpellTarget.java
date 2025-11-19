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
	FRIEND_AOE(false),
	FRIENDS_PARTY(true),

	PARTY(false),
	PARTY_AOE(false),

	ENEMY(true),
	ENEMY_AOE(false),

	TARGET(true),
	ATTACKER(true),

	GROUND(false),
	;

	private final boolean single;

	public static SpellTarget parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public boolean isAoE() {
		return !single;
	}
}
