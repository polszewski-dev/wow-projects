package wow.character.service;

import wow.character.model.build.Talents;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.PlayerCharacter;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
@FunctionalInterface
public interface PlayerCharacterFactory<T extends PlayerCharacter> {
	T newPlayerCharacter(
			String name,
			Phase phase,
			CharacterClass characterClass,
			Race race,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents
	);
}
