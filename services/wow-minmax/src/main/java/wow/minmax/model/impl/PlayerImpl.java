package wow.minmax.model.impl;

import wow.character.model.build.Talents;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.talent.TalentTree;
import wow.minmax.model.Player;
import wow.minmax.model.Unit;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
public class PlayerImpl extends PlayerCharacterImpl implements Player {
	public PlayerImpl(
			Phase phase,
			CharacterClass characterClass,
			Race race,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents
	) {
		super(phase, characterClass, race, level, baseStatInfo, combatRatingInfo, talents);
	}

	@Override
	public Unit getTarget() {
		return (Unit) super.getTarget();
	}

	@Override
	public void setTarget(Character target) {
		if (target != null && !(target instanceof Unit)) {
			throw new IllegalArgumentException();
		}
		super.setTarget(target);
	}

	@Override
	public int getNumberOfEffectsOnTarget(TalentTree tree) {
		return 0;
	}
}
