package wow.evaluator.model.impl;

import wow.character.model.build.Build;
import wow.character.model.build.Talents;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.model.equipment.Equipment;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.talent.TalentTree;
import wow.evaluator.model.Player;
import wow.evaluator.model.Unit;

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

	protected PlayerImpl(
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
			ExclusiveFactions exclusiveFactions,
			Consumables consumables
	) {
		super(phase, characterClass, level, baseStatInfo, combatRatingInfo, spellbook, buffs, race, build, equipment, professions, exclusiveFactions, consumables);
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
	public PlayerImpl copy() {
		var copy = new PlayerImpl(
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
				getExclusiveFactions().copy(),
				getConsumables().copy()
		);
		copy.setTarget(getTarget());
		return copy;
	}

	@Override
	public int getNumberOfEffectsOnTarget(TalentTree tree) {
		return 0;
	}
}
