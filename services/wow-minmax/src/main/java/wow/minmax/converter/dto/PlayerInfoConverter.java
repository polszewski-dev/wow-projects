package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.minmax.client.dto.PlayerInfoDTO;
import wow.minmax.client.dto.RaceDTO;
import wow.minmax.model.CharacterId;
import wow.minmax.model.Player;
import wow.minmax.model.config.ScriptInfo;
import wow.minmax.repository.MinmaxConfigRepository;

/**
 * User: POlszewski
 * Date: 2023-04-09
 */
@Component
@AllArgsConstructor
public class PlayerInfoConverter implements ParametrizedConverter<Player, PlayerInfoDTO, CharacterId> {
	private final CharacterClassConverter characterClassConverter;
	private final RaceConverter raceConverter;
	private final RacialConverter racialConverter;
	private final ProfessionConverter professionConverter;
	private final ScriptInfoConverter scriptInfoConverter;
	private final MinmaxConfigRepository minmaxConfigRepository;

	@Override
	public PlayerInfoDTO doConvert(Player source, CharacterId characterId) {
		return new PlayerInfoDTO(
				characterId.toString(),
				characterClassConverter.convert(source.getCharacterClass()),
				getRace(source),
				professionConverter.convertList(source.getProfessions().getList()),
				source.getTalentLink(),
				scriptInfoConverter.convert(getScript(source))
		);
	}

	private RaceDTO getRace(Player source) {
		var racials = racialConverter.convertList(source.getRacials());

		return raceConverter
				.convert(source.getRace())
				.withRacials(racials);
	}

	private ScriptInfo getScript(Player source) {
		var scriptPath = source.getBuild().getScript();

		return minmaxConfigRepository.getScript(scriptPath, source).orElseThrow();
	}
}
