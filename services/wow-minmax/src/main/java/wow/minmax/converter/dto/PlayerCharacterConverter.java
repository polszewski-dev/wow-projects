package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.PlayerCharacter;
import wow.commons.client.converter.ParametrizedConverter;
import wow.minmax.client.dto.PlayerCharacterDTO;
import wow.minmax.client.dto.RaceDTO;
import wow.minmax.model.CharacterId;

/**
 * User: POlszewski
 * Date: 2023-04-09
 */
@Component
@AllArgsConstructor
public class PlayerCharacterConverter implements ParametrizedConverter<PlayerCharacter, PlayerCharacterDTO, CharacterId> {
	private final CharacterClassConverter characterClassConverter;
	private final RaceConverter raceConverter;
	private final RacialConverter racialConverter;

	@Override
	public PlayerCharacterDTO doConvert(PlayerCharacter source, CharacterId characterId) {
		return new PlayerCharacterDTO(
				characterId.toString(),
				characterClassConverter.convert(source.getCharacterClass()),
				getRace(source)
		);
	}

	private RaceDTO getRace(PlayerCharacter source) {
		var racials = racialConverter.convertList(source.getRacials());

		return raceConverter
				.convert(source.getRace())
				.withRacials(racials);
	}
}
