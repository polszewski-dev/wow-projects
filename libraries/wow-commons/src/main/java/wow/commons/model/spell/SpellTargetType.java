package wow.commons.model.spell;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-08-15
 */
@AllArgsConstructor
public enum SpellTargetType {
	SELF,
	PET,

	FRIEND,
	FRIEND_AOE,
	FRIENDS_PARTY,

	PARTY,
	PARTY_AOE,

	ENEMY,
	ENEMY_AOE,
	ENEMY_AOE_EXCEPT_TARGET,

	ANY,

	TARGET,
	ATTACKER,

	GROUND,
	;

	public static SpellTargetType parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public boolean isSingle() {
		return switch (this) {
			case SELF, PET, FRIEND, FRIENDS_PARTY, ENEMY, ANY, TARGET, ATTACKER -> true;
			default -> false;
		};
	}

	public boolean isAoE() {
		return !isSingle();
	}
}
