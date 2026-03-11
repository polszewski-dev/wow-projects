package wow.simulator.model.unit;

import wow.character.model.character.Party;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.Raid;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface Player extends Unit, PlayerCharacter {
	Party<Player> getParty();

	default Raid<Player> getRaid() {
		return getParty().getRaid();
	}

	default List<Unit> getPartyMembers() {
		return (List<Unit>) (List<?>) getParty().getMembers();
	}
}
