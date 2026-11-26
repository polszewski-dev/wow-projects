package wow.character.service;

import wow.character.model.character.*;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.talent.Talent;

import java.util.List;

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
			List<Talent> availableTalents,
			CharacterProfessions characterProfessions,
			ExclusiveFactions exclusiveFactions
	);
}
