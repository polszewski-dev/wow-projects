package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.Converter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.persistent.PlayerProfilePO;

import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
@Component
@AllArgsConstructor
public class PlayerProfilePOConverter implements Converter<PlayerProfile, PlayerProfilePO>, BackConverter<PlayerProfile, PlayerProfilePO> {
	@Override
	public PlayerProfilePO doConvert(PlayerProfile source) {
		return new PlayerProfilePO(
				source.getProfileId().toString(),
				source.getProfileName(),
				source.getCharacterClassId(),
				source.getRaceId(),
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
				source.getLastModified(),
				CharacterId.parse(source.getLastModifiedCharacterId())
		);
	}
}
