package wow.character.model.character.impl;

import lombok.Getter;
import wow.character.model.character.*;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.talent.Talent;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
@Getter
public class PlayerCharacterImpl extends UnitImpl implements PlayerCharacter {
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Assets assets;

	public PlayerCharacterImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			Race race,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			List<Talent> availableTalents,
			CharacterProfessions professions,
			ExclusiveFactions exclusiveFactions
	) {
		super(name, phase, characterClass, level, race.getCreatureType(), race, race.getSide(), baseStatInfo, combatRatingInfo, availableTalents);
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
		this.assets = new Assets(this);
	}
}
