package wow.minmax.model.impl;

import wow.character.model.build.Build;
import wow.character.model.build.Talents;
import wow.character.model.character.*;
import wow.character.model.equipment.Equipment;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.minmax.model.Character;
import wow.minmax.model.PlayerCharacter;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
public class PlayerCharacterImpl extends wow.character.model.character.impl.PlayerCharacterImpl implements PlayerCharacter {
	public PlayerCharacterImpl(
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

	protected PlayerCharacterImpl(
			Phase phase,
			CharacterClass characterClass,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Spellbook spellbook,
			Buffs buffs,
			Race race,
			Build build,
			Equipment equipment,
			CharacterProfessions professions,
			ExclusiveFactions exclusiveFactions
	) {
		super(phase, characterClass, level, baseStatInfo, combatRatingInfo, spellbook, buffs, race, build, equipment, professions, exclusiveFactions);
	}

	@Override
	public Character getTarget() {
		return (Character) super.getTarget();
	}

	@Override
	public void setTarget(wow.character.model.character.Character target) {
		if (target != null && !(target instanceof Character)) {
			throw new IllegalArgumentException();
		}
		super.setTarget(target);
	}

	@Override
	public PlayerCharacterImpl copy() {
		var copy = new PlayerCharacterImpl(
				getPhase(),
				getCharacterClass(),
				getLevel(),
				getBaseStatInfo(),
				getCombatRatingInfo(),
				getSpellbook().copy(),
				getBuffs().copy(),
				getRace(),
				getBuild().copy(),
				getEquipment().copy(),
				getProfessions().copy(),
				getExclusiveFactions().copy()
		);
		copy.setTarget(getTarget());
		return copy;
	}
}
