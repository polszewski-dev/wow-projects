package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.unit.CharacterInfo;
import wow.minmax.converter.Converter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.BuildRepository;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class PlayerPOProfileConverter extends Converter<PlayerProfile, PlayerProfilePO> {
	private final EquipmentPOConverter equipmentPOConverter;
	private final BuffPOConverter buffPOConverter;
	private final BuildRepository buildRepository;

	@Override
	protected PlayerProfilePO doConvert(PlayerProfile playerProfile) {
		return new PlayerProfilePO(
				playerProfile.getProfileId(),
				playerProfile.getProfileName(),
				playerProfile.getCharacterClass(),
				playerProfile.getRace(),
				playerProfile.getLevel(),
				playerProfile.getEnemyType(),
				playerProfile.getPhase(),
				playerProfile.getBuild().getBuildId(),
				equipmentPOConverter.convert(playerProfile.getEquipment()),
				buffPOConverter.convertList(playerProfile.getBuffs()),
				playerProfile.getLastModified()
		);
	}

	@Override
	protected PlayerProfile doConvertBack(PlayerProfilePO value) {
		PlayerProfile playerProfile = new PlayerProfile(
				value.getProfileId(),
				value.getProfileName(),
				new CharacterInfo(value.getCharacterClass(), value.getRace(), value.getLevel(), List.of()),
				value.getEnemyType(),
				value.getPhase(),
				buildRepository.getBuild(value.getBuildId()).orElseThrow()
		);

		playerProfile.setEquipment(equipmentPOConverter.convertBack(value.getEquipment()));
		playerProfile.setBuffs(buffPOConverter.convertBackList(value.getBuffs()));

		playerProfile.setLastModified(value.getLastModified());

		return playerProfile;
	}
}
