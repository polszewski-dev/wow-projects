package wow.simulator.model.unit.impl;

import lombok.Getter;
import wow.character.model.character.*;
import wow.character.model.talent.Talents;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.UnitParty;
import wow.simulator.model.unit.UnitRaid;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@Getter
public class PlayerImpl extends UnitImpl implements Player, UnitParty.OnAddRemove<Player> {
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Assets assets;

	@Getter
	private UnitParty<Player> party;

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

		UnitRaid.newUnitRaid(this);
	}

	@Override
	public void onAddedToParty(UnitParty<Player> party) {
		if (this.party != null) {
			this.party.remove(this);
		}
		this.party = party;
	}

	@Override
	public void onRemovedFromParty() {
		UnitRaid.newUnitRaid(this);
	}
}
