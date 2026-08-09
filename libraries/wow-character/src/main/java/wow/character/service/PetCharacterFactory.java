package wow.character.service;

import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.PetCharacter;
import wow.character.model.talent.Talents;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.PetType;
import wow.commons.model.character.Race;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
@FunctionalInterface
public interface PetCharacterFactory<T extends PetCharacter> {
	T newPetCharacter(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			PetType petType,
			Race race,
			Side side,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents,
			CharacterRestriction characterRestriction
	);
}
