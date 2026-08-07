package wow.character.model.character.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.NonPlayerCharacter;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;

import static wow.character.model.character.BaseStatInfo.getDummyBaseStatInfo;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
@Getter
@Setter
public class NonPlayerCharacterImpl extends CharacterImpl implements NonPlayerCharacter {
	private Percent healthPct = Percent._100;

	public NonPlayerCharacterImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			CreatureType creatureType,
			Side side,
			CombatRatingInfo combatRatingInfo
	) {
		super(name, phase, characterClass, level, creatureType, side, getDummyBaseStatInfo(characterClass, level, phase), combatRatingInfo);
	}
}
