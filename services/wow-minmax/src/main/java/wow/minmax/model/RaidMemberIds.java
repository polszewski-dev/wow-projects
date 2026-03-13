package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.Raid;

import java.util.List;
import java.util.stream.IntStream;

/**
 * User: POlszewski
 * Date: 2026-03-13
 */
@AllArgsConstructor
@Getter
@Setter
public class RaidMemberIds {
	private PlayerId playerId;
	private List<PartyMemberIds> parties;

	public RaidMemberIds(PlayerId playerId) {
		this.playerId = playerId;
		this.parties = IntStream.range(0, Raid.MAX_PARTIES)
				.mapToObj(x -> new PartyMemberIds())
				.toList();

		getParty(0).addMember(playerId);
	}

	public PartyMemberIds getParty(int partyIdx) {
		return parties.get(partyIdx);
	}
}
