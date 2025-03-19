package wow.minmax.model.impl;

import lombok.Getter;
import wow.character.model.build.Build;
import wow.character.model.build.Talents;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.character.impl.CharacterImpl;
import wow.character.model.equipment.Equipment;
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
@Getter
public class PlayerImpl extends CharacterImpl implements Player {
	private final Race race;
	private final Build build;
	private final Equipment equipment;
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Consumables consumables;

	public PlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			Race race,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents
	) {
		super(name, phase, characterClass, level, baseStatInfo, combatRatingInfo);
		this.race = race;
		this.build = new Build(phase.getGameVersion(), talents);
		this.equipment = new Equipment();
		this.professions = new CharacterProfessions();
		this.exclusiveFactions = new ExclusiveFactions();
		this.consumables = new Consumables();
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
