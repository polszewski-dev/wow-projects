package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.Raid;
import wow.minmax.converter.db.RaidConfigConverter;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;
import wow.minmax.model.RaidMemberIds;
import wow.minmax.repository.RaidConfigRepository;
import wow.minmax.service.PlayerService;
import wow.minmax.service.RaidService;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-14
 */
@Service
@AllArgsConstructor
public class RaidServiceImpl implements RaidService {
	private final RaidConfigRepository raidConfigRepository;
	private final RaidConfigConverter raidConfigConverter;

	private final PlayerService playerService;

	@Override
	public Raid<Player> getRaid(PlayerId playerId) {
		var raidMemberIds = getRaidMemberIds(playerId);

		var raid = new Raid<Player>();

		var parties = raidMemberIds.getParties();

		for (int partyIdx = 0; partyIdx < parties.size(); partyIdx++) {
			var party = parties.get(partyIdx);

			for (var partyMemberId : party.getMembers()) {
				var partyMember = playerService.getPlayer(partyMemberId);

				raid.getParty(partyIdx).add(partyMember);
			}
		}

		return raid;
	}

	@Override
	public Raid<Player> getRaid(Player player) {
		return getRaid(player.getPlayerId());
	}

	@Override
	public List<Player> getAvailablePartyMembers(PlayerId playerId) {
		return List.of();
	}

	@Override
	public void addPartyMember(PlayerId playerId, int partyIdx, PlayerId partyMemberId) {
		var raidMemberIds = getRaidMemberIds(playerId);

		raidMemberIds.getParty(partyIdx).addMember(partyMemberId);

		saveRaidMemberIds(raidMemberIds);
	}

	@Override
	public void removePartyMember(PlayerId playerId, int partyIdx, int partyMemberIdx) {
		var raidMemberIds = getRaidMemberIds(playerId);

		raidMemberIds.getParty(partyIdx).removeMember(partyMemberIdx);

		saveRaidMemberIds(raidMemberIds);
	}

	private RaidMemberIds getRaidMemberIds(PlayerId playerId) {
		return raidConfigRepository.findById(playerId.toString())
				.map(raidConfigConverter::convertBack)
				.orElseGet(() -> new RaidMemberIds(playerId));
	}

	private void saveRaidMemberIds(RaidMemberIds raidMemberIds) {
		var raidConfig = raidConfigConverter.convert(raidMemberIds);

		raidConfigRepository.save(raidConfig);
	}
}
