package wow.simulator.model.unit;

import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.Party;
import wow.character.model.character.Raid;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface NonPlayer extends Unit, NonPlayerCharacter {
	Party<NonPlayer> getParty();

	default Raid<NonPlayer> getRaid() {
		return getParty().getRaid();
	}

	default List<Unit> getPartyMembers() {
		return (List<Unit>) (List<?>) getParty().getMembers();
	}
}
