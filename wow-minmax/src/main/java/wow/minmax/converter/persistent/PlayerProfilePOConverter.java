package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.Character;
import wow.minmax.converter.BackConverter;
import wow.minmax.converter.Converter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.persistent.CharacterPO;
import wow.minmax.model.persistent.PlayerProfilePO;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
@Component
@AllArgsConstructor
public class PlayerProfilePOConverter implements Converter<PlayerProfile, PlayerProfilePO>, BackConverter<PlayerProfile, PlayerProfilePO> {
	private final CharacterPOConverter characterPOConverter;

	@Override
	public PlayerProfilePO doConvert(PlayerProfile source) {
		return new PlayerProfilePO(
				source.getProfileId(),
				source.getProfileName(),
				source.getCharacterClassId(),
				source.getRaceId(),
				convertCharacters(source.getCharacterByKey()),
				source.getLastModified(),
				source.getLastModifiedCharacterId()
		);
	}

	@Override
	public PlayerProfile doConvertBack(PlayerProfilePO source) {
		return new PlayerProfile(
				source.getProfileId(),
				source.getProfileName(),
				source.getCharacterClassId(),
				source.getRaceId(),
				convertBackCharacters(source.getCharacterByKey()),
				source.getLastModified(),
				source.getLastModifiedCharacterId()
		);
	}

	private Map<CharacterId, CharacterPO> convertCharacters(Map<CharacterId, Character> characterByKey) {
		return characterByKey.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						e -> characterPOConverter.convert(e.getValue())
				));
	}

	private Map<CharacterId, Character> convertBackCharacters(Map<CharacterId, CharacterPO> characterByKey) {
		return characterByKey.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						e -> characterPOConverter.convertBack(e.getValue())
				));
	}
}
