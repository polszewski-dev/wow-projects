package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.Raid;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.RaidMembersDTO;
import wow.minmax.model.Player;

/**
 * User: POlszewski
 * Date: 2026-03-13
 */
@Component
@AllArgsConstructor
public class RaidMembersConverter implements Converter<Raid<Player>, RaidMembersDTO> {
	private final PartyMembersConverter partyMembersConverter;

	@Override
	public RaidMembersDTO doConvert(Raid<Player> source) {
		var parties = source.getParties().stream()
				.map(partyMembersConverter::convert)
				.toList();

		return new RaidMembersDTO(parties);
	}
}
