package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import wow.minmax.client.dto.PartyMemberDTO;
import wow.minmax.client.dto.RaidMembersDTO;
import wow.minmax.converter.dto.PartyMemberConverter;
import wow.minmax.converter.dto.RaidMembersConverter;
import wow.minmax.model.PlayerId;
import wow.minmax.service.RaidService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-13
 */
@RestController
@RequestMapping("api/v1/raids")
@AllArgsConstructor
public class RaidController {
	private final RaidService raidService;

	private final RaidMembersConverter raidMembersConverter;
	private final PartyMemberConverter partyMemberConverter;

	@GetMapping("{playerId}")
	public RaidMembersDTO getRaidMembers(
			@PathVariable("playerId") PlayerId playerId
	) {
		var raid = raidService.getRaid(playerId);

		return raidMembersConverter.convert(raid);
	}

	@GetMapping("{playerId}/available-members")
	public List<PartyMemberDTO> getAvailablePartyMembers(
			@PathVariable("playerId") PlayerId playerId
	) {
		var availablePartyMembers = raidService.getAvailablePartyMembers(playerId);

		return partyMemberConverter.convertList(availablePartyMembers);
	}

	@PutMapping("{playerId}/{partyIdx}/{partyMemberId}")
	public void addPartyMember(
			@PathVariable("playerId") PlayerId playerId,
			@PathVariable("partyIdx") int partyIdx,
			@PathVariable("partyMemberId") PlayerId partyMemberId
	) {
		raidService.addPartyMember(playerId, partyIdx, partyMemberId);
	}

	@DeleteMapping("{playerId}/{partyIdx}/{partyMemberIdx}")
	public void removePartyMember(
			@PathVariable("playerId") PlayerId playerId,
			@PathVariable("partyIdx") int partyIdx,
			@PathVariable("partyMemberIdx") int partyMemberIdx
	) {
		raidService.removePartyMember(playerId, partyIdx, partyMemberIdx);
	}
}
