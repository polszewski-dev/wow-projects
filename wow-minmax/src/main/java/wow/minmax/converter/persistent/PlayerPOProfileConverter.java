package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.repository.SpellDataRepository;
import wow.minmax.converter.Converter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.BuildRepository;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class PlayerPOProfileConverter extends Converter<PlayerProfile, PlayerProfilePO> {
	private final EquipmentPOConverter equipmentPOConverter;
	private final BuffPOConverter buffPOConverter;
	private final SpellDataRepository spellDataRepository;
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
				value.getCharacterClass(),
				value.getRace(),
				value.getLevel(),
				value.getEnemyType(),
				value.getPhase(),
				buildRepository.getBuild(value.getBuildId())
		);

		playerProfile.setEquipment(equipmentPOConverter.convertBack(value.getEquipment()));
		playerProfile.setBuffs(buffPOConverter.convertBackList(value.getBuffs()));

		playerProfile.setLastModified(value.getLastModified());

		return playerProfile;
	}
}
