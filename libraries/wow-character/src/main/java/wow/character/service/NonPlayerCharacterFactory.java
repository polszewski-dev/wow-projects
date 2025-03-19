package wow.character.service;

import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.NonPlayerCharacter;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
@FunctionalInterface
public interface NonPlayerCharacterFactory<T extends NonPlayerCharacter> {
	T newPlayerCharacter(
			String name,
			Phase phase,
			CharacterClass characterClass,
			CreatureType creatureType,
			int level,
			CombatRatingInfo combatRatingInfo
	);
}
