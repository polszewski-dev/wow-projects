package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.service.PlayerProfileService;

import java.util.Map;

import static wow.minmax.converter.persistent.PoConverterParams.getPlayerProfileService;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
@Component
@AllArgsConstructor
public class PlayerProfilePOConverter implements Converter<PlayerProfile, PlayerProfilePO>, ParametrizedBackConverter<PlayerProfile, PlayerProfilePO> {
	private final EquipmentPOConverter equipmentPOConverter;
	private final CharacterProfessionPOConverter characterProfessionPOConverter;
	private final BuffPOConverter buffPOConverter;

	@Override
	public PlayerProfilePO doConvert(PlayerProfile playerProfile) {
		return new PlayerProfilePO(
				playerProfile.getProfileId(),
				playerProfile.getProfileName(),
				playerProfile.getCharacterClass(),
				playerProfile.getRace(),
				playerProfile.getLevel(),
				playerProfile.getBuildId(),
				characterProfessionPOConverter.convertList(playerProfile.getProfessions().getList()),
				playerProfile.getEnemyType(),
				playerProfile.getPhase(),
				equipmentPOConverter.convert(playerProfile.getEquipment()),
				buffPOConverter.convertList(playerProfile.getBuffs().getList()),
				playerProfile.getLastModified()
		);
	}

	@Override
	public PlayerProfile doConvertBack(PlayerProfilePO value, Map<String, Object> params) {
		PlayerProfileService playerProfileService = getPlayerProfileService(params);

		PlayerProfile playerProfile = playerProfileService.createTemporaryPlayerProfile(
				value.getProfileId(),
				value.getProfileName(),
				value.getCharacterClassId(),
				value.getRace(),
				value.getLevel(),
				value.getBuildId(),
				characterProfessionPOConverter.convertBackList(value.getProfessions(), params),
				value.getEnemyType(),
				value.getPhaseId()
		);

		playerProfile.setEquipment(equipmentPOConverter.convertBack(value.getEquipment(), params));
		playerProfile.setBuffs(buffPOConverter.convertBackList(value.getBuffs(), params));
		playerProfile.setLastModified(value.getLastModified());
		return playerProfile;
	}
}
