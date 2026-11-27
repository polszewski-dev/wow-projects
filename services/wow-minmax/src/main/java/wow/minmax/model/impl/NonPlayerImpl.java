package wow.minmax.model.impl;

import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CombatRatingInfo;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.talent.Talent;
import wow.minmax.model.NonPlayer;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-02-09
 */
public class NonPlayerImpl extends UnitImpl implements NonPlayer {
	public NonPlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			CreatureType creatureType,
			Race race,
			Side side,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			List<Talent> availableTalents
	) {
		super(name, phase, characterClass, level, creatureType, race, side, baseStatInfo, combatRatingInfo, availableTalents);
	}
}
