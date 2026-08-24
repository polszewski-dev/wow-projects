package wow.simulator.model.unit;

import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.Raid;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface Player extends Unit, PlayerCharacter {
	@Override
	UnitParty<Player> getParty();

	@Override
	default Raid<Player> getRaid() {
		return getParty().getRaid();
	}

	@Override
	default List<Player> getPartyMembers() {
		return getParty().getMembers();
	}

	default void invite(Player... players) {
		getParty().add(players);
	}
}
