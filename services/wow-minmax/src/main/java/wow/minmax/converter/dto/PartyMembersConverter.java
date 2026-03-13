package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.Party;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.PartyMembersDTO;
import wow.minmax.model.Player;

/**
 * User: POlszewski
 * Date: 2026-03-13
 */
@Component
@AllArgsConstructor
public class PartyMembersConverter implements Converter<Party<Player>, PartyMembersDTO> {
	private final PartyMemberConverter partyMemberConverter;

	@Override
	public PartyMembersDTO doConvert(Party<Player> source) {
		var members = source.getMembers().stream()
				.map(partyMemberConverter::convert)
				.toList();

		return new PartyMembersDTO(members);
	}
}
