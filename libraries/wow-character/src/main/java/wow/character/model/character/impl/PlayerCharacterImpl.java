package wow.character.model.character.impl;

import lombok.Getter;
import wow.character.model.build.Build;
import wow.character.model.build.Talents;
import wow.character.model.character.*;
import wow.character.model.equipment.Equipment;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
@Getter
public class PlayerCharacterImpl extends CharacterImpl implements PlayerCharacter {
	private final Race race;
	private final Build build;
	private final Equipment equipment;
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Consumables consumables;

	public PlayerCharacterImpl(String name, Phase phase, CharacterClass characterClass, Race race, int level, BaseStatInfo baseStatInfo, CombatRatingInfo combatRatingInfo, Talents talents) {
		super(name, phase, characterClass, level, baseStatInfo, combatRatingInfo);
		this.race = race;
		this.build = new Build(phase.getGameVersion(), talents);
		this.equipment = new Equipment();
		this.professions = new CharacterProfessions();
		this.exclusiveFactions = new ExclusiveFactions();
		this.consumables = new Consumables();
	}
}
