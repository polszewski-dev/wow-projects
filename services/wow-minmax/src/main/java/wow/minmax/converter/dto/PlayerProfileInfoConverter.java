package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.repository.character.CharacterClassRepository;
import wow.commons.repository.character.RaceRepository;
import wow.minmax.config.ProfileConfig;
import wow.minmax.converter.BackConverter;
import wow.minmax.converter.Converter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.dto.PlayerProfileInfoDTO;

/**
 * User: POlszewski
 * Date: 2022-12-27
 */
@Component
@AllArgsConstructor
public class PlayerProfileInfoConverter implements Converter<PlayerProfileInfo, PlayerProfileInfoDTO>, BackConverter<PlayerProfileInfo, PlayerProfileInfoDTO> {
	private final CharacterClassConverter characterClassConverter;
	private final RaceConverter raceConverter;

	private final CharacterClassRepository characterClassRepository;
	private final RaceRepository raceRepository;

	private final ProfileConfig profileConfig;

	@Override
	public PlayerProfileInfoDTO doConvert(PlayerProfileInfo source) {
		var latestSupportedVersionId = profileConfig.getLatestSupportedVersionId();
		var characterClass = characterClassRepository.getCharacterClass(source.getCharacterClassId(), latestSupportedVersionId).orElseThrow();
		var race = raceRepository.getRace(source.getRaceId(), latestSupportedVersionId).orElseThrow();

		return new PlayerProfileInfoDTO(
				source.getProfileId(),
				source.getProfileName(),
				characterClassConverter.convert(characterClass),
				raceConverter.convert(race),
				source.getLastModified(),
				source.getLastUsedCharacterId().toString()
		);
	}

	@Override
	public PlayerProfileInfo doConvertBack(PlayerProfileInfoDTO source) {
		return new PlayerProfileInfo(
				source.getProfileId(),
				source.getProfileName(),
				source.getCharacterClass().getId(),
				source.getRace().getId(),
				source.getLastModified(),
				CharacterId.parse(source.getLastUsedCharacterId())
		);
	}
}
