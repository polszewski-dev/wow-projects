package wow.character.model.character.impl;

import lombok.Getter;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.NonPlayerCharacter;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;

import static wow.character.model.character.BaseStatInfo.getDummyBaseStatInfo;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
@Getter
public class NonPlayerCharacterImpl extends CharacterImpl implements NonPlayerCharacter {
	private final CreatureType creatureType;

	public NonPlayerCharacterImpl(Phase phase, CharacterClass characterClass, CreatureType creatureType, int level, CombatRatingInfo combatRatingInfo) {
		super(phase, characterClass, level, getDummyBaseStatInfo(characterClass, level, phase), combatRatingInfo);
		this.creatureType = creatureType;
	}
}
