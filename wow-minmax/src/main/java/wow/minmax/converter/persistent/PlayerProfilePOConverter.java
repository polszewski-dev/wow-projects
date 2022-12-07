package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.minmax.converter.ParametrizedConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.service.PlayerProfileService;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-07
 */
@Component
@AllArgsConstructor
public class PlayerProfilePOConverter extends ParametrizedConverter<PlayerProfile, PlayerProfilePO> {
	private final EquipmentPOConverter equipmentPOConverter;
	private final CharacterProfessionPOConverter characterProfessionPOConverter;
	private final BuffPOConverter buffPOConverter;

	public static final String PARAM_PLAYER_PROFILE_SERVICE = "playerProfileService";
	public static final String PARAM_PHASE = "phase";

	@Override
	protected PlayerProfilePO doConvert(PlayerProfile playerProfile, Map<String, Object> params) {
		return new PlayerProfilePO(
				playerProfile.getProfileId(),
				playerProfile.getProfileName(),
				playerProfile.getCharacterClass(),
				playerProfile.getRace(),
				playerProfile.getLevel(),
				playerProfile.getBuild().getBuildId(),
				characterProfessionPOConverter.convertList(playerProfile.getProfessions(), params),
				playerProfile.getEnemyType(),
				playerProfile.getPhase(),
				equipmentPOConverter.convert(playerProfile.getEquipment(), params),
				buffPOConverter.convertList(playerProfile.getBuffs(), params),
				playerProfile.getLastModified()
		);
	}

	@Override
	protected PlayerProfile doConvertBack(PlayerProfilePO value, Map<String, Object> params) {
		PlayerProfileService playerProfileService = (PlayerProfileService)params.get(PARAM_PLAYER_PROFILE_SERVICE);

		PlayerProfile playerProfile = playerProfileService.createTemporaryPlayerProfile(
				value.getProfileId(),
				value.getProfileName(),
				value.getCharacterClass(),
				value.getRace(),
				value.getLevel(),
				value.getBuildId(),
				characterProfessionPOConverter.convertBackList(value.getProfessions(), Map.of()),
				value.getEnemyType(),
				value.getPhase()
		);

		playerProfile.setEquipment(equipmentPOConverter.convertBack(value.getEquipment(), params));
		playerProfile.setBuffs(buffPOConverter.convertBackList(value.getBuffs(), params));
		playerProfile.setLastModified(value.getLastModified());
		return playerProfile;
	}
}
