package wow.simulator.model.unit.impl;

import lombok.Getter;
import wow.character.model.character.*;
import wow.character.model.talent.Talents;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.simulator.model.unit.Player;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Getter
public class PlayerImpl extends UnitImpl implements Player, Party.OnAdd<Player> {
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Assets assets;

	@Getter
	private Party<Player> party;

	public PlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			Race race,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents,
			CharacterProfessions professions,
			ExclusiveFactions exclusiveFactions
	) {
		super(name, phase, characterClass, level, race.getCreatureType(), race, race.getSide(), baseStatInfo, combatRatingInfo, talents);
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
		this.assets = new Assets();

		Raid.newRaid(this);
	}

	@Override
	public void onAdd(Party<Player> party) {
		if (this.party != null) {
			this.party.remove(this);
		}
		this.party = party;
	}
}
