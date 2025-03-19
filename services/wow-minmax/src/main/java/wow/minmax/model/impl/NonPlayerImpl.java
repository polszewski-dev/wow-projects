package wow.minmax.model.impl;

import lombok.Getter;
import wow.character.model.character.Character;
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
 * Date: 2024-11-20
 */
@Getter
public class NonPlayerImpl extends CharacterImpl implements NonPlayer {
	private final CreatureType creatureType;

	public NonPlayerImpl(
			Phase phase,
			CharacterClass characterClass,
			CreatureType creatureType,
			int level,
			CombatRatingInfo combatRatingInfo
	) {
		super(phase, characterClass, level, getDummyBaseStatInfo(characterClass, level, phase), combatRatingInfo);
		this.creatureType = creatureType;
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
}
