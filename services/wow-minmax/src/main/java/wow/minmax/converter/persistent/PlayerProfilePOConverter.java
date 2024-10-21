package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.PlayerCharacter;
import wow.minmax.converter.BackConverter;
import wow.minmax.converter.Converter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.persistent.PlayerCharacterPO;
import wow.minmax.model.persistent.PlayerProfilePO;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
@Component
@AllArgsConstructor
public class PlayerProfilePOConverter implements Converter<PlayerProfile, PlayerProfilePO>, BackConverter<PlayerProfile, PlayerProfilePO> {
	private final PlayerCharacterPOConverter playerCharacterPOConverter;

	@Override
	public PlayerProfilePO doConvert(PlayerProfile source) {
		return new PlayerProfilePO(
				source.getProfileId().toString(),
				source.getProfileName(),
				source.getCharacterClassId(),
				source.getRaceId(),
				convertCharacters(source.getCharacterByKey()),
				source.getLastModified(),
				source.getLastModifiedCharacterId().toString()
		);
	}

	@Override
	public PlayerProfile doConvertBack(PlayerProfilePO source) {
		return new PlayerProfile(
				UUID.fromString(source.getProfileId()),
				source.getProfileName(),
				source.getCharacterClassId(),
				source.getRaceId(),
				convertBackCharacters(source.getCharacterByKey()),
				source.getLastModified(),
				CharacterId.parse(source.getLastModifiedCharacterId())
		);
	}

	private Map<String, PlayerCharacterPO> convertCharacters(Map<CharacterId, PlayerCharacter> characterByKey) {
		return characterByKey.entrySet().stream()
				.collect(Collectors.toMap(
						t -> t.getKey().toString(),
						e -> playerCharacterPOConverter.convert(e.getValue(), e.getKey())
				));
	}

	private Map<CharacterId, PlayerCharacter> convertBackCharacters(Map<String, PlayerCharacterPO> characterByKey) {
		return characterByKey.entrySet().stream()
				.collect(Collectors.toMap(
						t -> CharacterId.parse(t.getKey()),
						e -> playerCharacterPOConverter.convertBack(e.getValue())
				));
	}
}
