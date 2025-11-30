package wow.commons.model.spell;

import wow.commons.model.Duration;
import wow.commons.model.config.CharacterRestricted;

/**
 * User: POlszewski
 * Date: 2023-10-04
 */
public interface Ability extends Spell, CharacterRestricted {
	AbilityId getAbilityId();

	AbilityCategory getCategory();

	CastInfo getCastInfo();

	Cost getCost();

	int getRange();

	AbilityId getEffectRemovedOnHit();

	default Duration getCastTime() {
		return getCastInfo().castTime();
	}

	default boolean isChanneled() {
		return getCastInfo().channeled();
	}

	int getRank();

	AbilityNameRank getNameRank();
}
