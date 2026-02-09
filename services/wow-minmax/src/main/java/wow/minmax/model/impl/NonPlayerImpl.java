package wow.minmax.model.impl;

import lombok.Getter;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.impl.CharacterImpl;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;
import wow.minmax.model.NonPlayer;
import wow.minmax.model.Unit;

import static wow.character.model.character.BaseStatInfo.getDummyBaseStatInfo;

/**
 * User: POlszewski
 * Date: 2026-02-09
 */
@Getter
public class NonPlayerImpl extends CharacterImpl implements NonPlayer {
	private final CreatureType creatureType;

	public NonPlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			CreatureType creatureType,
			int level,
			CombatRatingInfo combatRatingInfo
	) {
		super(name, phase, characterClass, level, getDummyBaseStatInfo(characterClass, level, phase), combatRatingInfo);
		this.creatureType = creatureType;
	}

	@Override
	public Unit getTarget() {
		return (Unit) super.getTarget();
	}
}
