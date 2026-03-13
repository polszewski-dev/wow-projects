package wow.minmax.service;

import wow.character.model.character.Raid;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-14
 */
public interface RaidService {
	Raid<Player> getRaid(PlayerId playerId);

	Raid<Player> getRaid(Player player);

	List<Player> getAvailablePartyMembers(PlayerId playerId);

	void addPartyMember(PlayerId playerId, int partyIdx, PlayerId partyMemberId);

	void removePartyMember(PlayerId playerId, int partyIdx, int partyMemberIdx);
}
