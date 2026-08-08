package wow.character.model.character.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.talent.Talents;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;

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
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents
	) {
		super(name, phase, characterClass, level, creatureType, side, baseStatInfo, combatRatingInfo, talents);
	}
}
