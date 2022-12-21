package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.minmax.converter.Converter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.dto.PlayerProfileDTO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class PlayerProfileConverter extends Converter<PlayerProfile, PlayerProfileDTO> {
	private final EquipmentConverter equipmentConverter;
	private final BuffConverter buffConverter;
	private final TalentConverter talentConverter;

	@Override
	protected PlayerProfileDTO doConvert(PlayerProfile playerProfile) {
		return new PlayerProfileDTO(
				playerProfile.getProfileId(),
				playerProfile.getProfileName(),
				playerProfile.getCharacterClass(),
				playerProfile.getRace(),
				playerProfile.getLevel(),
				playerProfile.getEnemyType(),
				playerProfile.getRole(),
				playerProfile.getPhase(),
				equipmentConverter.convert(playerProfile.getEquipment()),
				buffConverter.convertList(playerProfile.getBuffs().getList()),
				talentConverter.convertList(playerProfile.getTalents().getList()),
				playerProfile.getLastModified(),
				null
		);
	}
}
