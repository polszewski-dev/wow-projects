package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-13
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PartyMemberIds {
	private List<PlayerId> members = new ArrayList<>();

	public void addMember(PlayerId memberId) {
		members.add(memberId);
	}

	public void removeMember(int memberIdx) {
		members.remove(memberIdx);
	}
}
