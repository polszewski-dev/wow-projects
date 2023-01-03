package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.minmax.converter.Converter;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.dto.PlayerProfileInfoDTO;

/**
 * User: POlszewski
 * Date: 2022-12-27
 */
@Component
@AllArgsConstructor
public class PlayerProfileInfoConverter implements Converter<PlayerProfileInfo, PlayerProfileInfoDTO> {
	@Override
	public PlayerProfileInfoDTO doConvert(PlayerProfileInfo value) {
		return new PlayerProfileInfoDTO(
			value.getProfileId(),
			value.getProfileName(),
			value.getCharacterClass(),
			value.getRace(),
			value.getLevel(),
			value.getEnemyType(),
			value.getBuildId(),
			value.getPhase(),
			value.getLastModified()
		);
	}
}
