package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.minmax.converter.BackConverter;
import wow.minmax.converter.Converter;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.dto.PlayerProfileInfoDTO;

import static wow.minmax.converter.dto.DtoConverterParams.createParams;

/**
 * User: POlszewski
 * Date: 2022-12-27
 */
@Component
@AllArgsConstructor
public class PlayerProfileInfoConverter implements Converter<PlayerProfileInfo, PlayerProfileInfoDTO>, BackConverter<PlayerProfileInfo, PlayerProfileInfoDTO> {
	private final CharacterProfessionConverter characterProfessionConverter;

	@Override
	public PlayerProfileInfoDTO doConvert(PlayerProfileInfo source) {
		return new PlayerProfileInfoDTO(
				source.getProfileId(),
				source.getProfileName(),
				source.getCharacterClassId(),
				source.getRaceId(),
				source.getLevel(),
				source.getEnemyType(),
				source.getBuildId(),
				characterProfessionConverter.convertList(source.getProfessions()),
				source.getPhaseId(),
				source.getLastModified()
		);
	}

	@Override
	public PlayerProfileInfo doConvertBack(PlayerProfileInfoDTO source) {
		return new PlayerProfileInfo(
				source.getProfileId(),
				source.getProfileName(),
				source.getCharacterClass(),
				source.getRace(),
				source.getLevel(),
				source.getEnemyType(),
				source.getBuildId(),
				characterProfessionConverter.convertBackList(source.getProfessions(), createParams(source.getPhase())),
				source.getPhase(),
				source.getLastModified()
		);
	}
}
