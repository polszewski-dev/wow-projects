package wow.minmax.model.impl;

import lombok.Getter;
import wow.character.model.character.*;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.talent.Talent;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-02-09
 */
@Getter
public class PlayerImpl extends UnitImpl implements Player {
	private final PlayerId playerId;
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Assets assets;

	public PlayerImpl(
			PlayerId playerId,
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
		this.playerId = playerId;
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
		this.assets = new Assets(this);
	}

	public static PlayerCharacterFactory<Player> getFactory(PlayerId playerId) {
		return (name, phase, characterClass, race, level, baseStatInfo, combatRatingInfo, talents, professions, exclusiveFactions) -> new PlayerImpl(playerId, name, phase, characterClass, race, level, baseStatInfo, combatRatingInfo, talents, professions, exclusiveFactions);
	}
}
