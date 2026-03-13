package wow.minmax.converter.db;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.Converter;
import wow.minmax.model.PartyMemberIds;
import wow.minmax.model.PlayerId;
import wow.minmax.model.RaidMemberIds;
import wow.minmax.model.db.RaidConfig;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-13
 */
@Component
@AllArgsConstructor
public class RaidConfigConverter implements Converter<RaidMemberIds, RaidConfig>, BackConverter<RaidMemberIds, RaidConfig> {
	@Override
	public RaidConfig doConvert(RaidMemberIds source) {
		var playerId = source.getPlayerId().toString();
		var parties = source.getParties().stream()
				.map(PartyMemberIds::getMembers)
				.map(RaidConfigConverter::getMemberIdStrings)
				.toList();

		return new RaidConfig(playerId, parties);
	}

	@Override
	public RaidMemberIds doConvertBack(RaidConfig source) {
		var playerId = PlayerId.parse(source.getPlayerId());
		var parties = source.getParties().stream()
				.map(RaidConfigConverter::getMemberIds)
				.map(PartyMemberIds::new)
				.toList();

		return new RaidMemberIds(playerId, parties);
	}

	private static List<PlayerId> getMemberIds(List<String> party) {
		return party.stream()
				.map(PlayerId::parse)
				.toList();
	}

	private static List<String> getMemberIdStrings(List<PlayerId> members) {
		return members.stream()
				.map(PlayerId::toString)
				.toList();
	}
}
