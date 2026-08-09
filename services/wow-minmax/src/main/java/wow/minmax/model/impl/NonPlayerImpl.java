package wow.minmax.model.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.impl.CharacterImpl;
import wow.character.model.talent.Talents;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.minmax.model.NonPlayer;
import wow.minmax.model.Unit;

/**
 * User: POlszewski
 * Date: 2026-02-09
 */
@Getter
@Setter
public class NonPlayerImpl extends CharacterImpl implements NonPlayer {
	private Percent healthPct = Percent._100;

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
			Talents talents
	) {
		super(name, phase, characterClass, level, creatureType, race, side, baseStatInfo, combatRatingInfo, talents);
	}

	@Override
	public Unit getTarget() {
		return (Unit) super.getTarget();
	}
}
