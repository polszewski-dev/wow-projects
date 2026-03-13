package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.PartyMemberDTO;
import wow.minmax.model.Player;

/**
 * User: POlszewski
 * Date: 2026-03-13
 */
@Component
@AllArgsConstructor
public class PartyMemberConverter implements Converter<Player, PartyMemberDTO> {
	private final CharacterClassConverter characterClassConverter;
	private final RaceConverter raceConverter;

	@Override
	public PartyMemberDTO doConvert(Player source) {
		return new PartyMemberDTO(
				source.getName(),
				characterClassConverter.convert(source.getCharacterClass()),
				raceConverter.convert(source.getRace())
		);
	}
}
