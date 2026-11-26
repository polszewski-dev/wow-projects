package wow.character.service;

import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.PetCharacter;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.PetType;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.Talent;

import java.util.List;

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
			List<Talent> availableTalents,
			Spell sourceSpell
	);
}
