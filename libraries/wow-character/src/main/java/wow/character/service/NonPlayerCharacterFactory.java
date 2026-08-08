package wow.character.service;

import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.talent.Talents;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
@FunctionalInterface
public interface NonPlayerCharacterFactory<T extends NonPlayerCharacter> {
	T newNonPlayerCharacter(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			CreatureType creatureType,
			Side side,
			CombatRatingInfo combatRatingInfo,
			Talents talents
	);
}
