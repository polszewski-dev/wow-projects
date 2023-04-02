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
	public PlayerProfileInfoDTO doConvert(PlayerProfileInfo value) {
		return new PlayerProfileInfoDTO(
			value.getProfileId(),
			value.getProfileName(),
			value.getCharacterClassId(),
			value.getRaceId(),
			value.getLevel(),
			value.getEnemyType(),
			value.getBuildId(),
			characterProfessionConverter.convertList(value.getProfessions()),
			value.getPhaseId(),
			value.getLastModified()
		);
	}

	@Override
	public PlayerProfileInfo doConvertBack(PlayerProfileInfoDTO value) {
		return new PlayerProfileInfo(
			value.getProfileId(),
			value.getProfileName(),
			value.getCharacterClass(),
			value.getRace(),
			value.getLevel(),
			value.getEnemyType(),
			value.getBuildId(),
			characterProfessionConverter.convertBackList(value.getProfessions(), createParams(value.getPhase())),
			value.getPhase(),
			value.getLastModified()
		);
	}
}
