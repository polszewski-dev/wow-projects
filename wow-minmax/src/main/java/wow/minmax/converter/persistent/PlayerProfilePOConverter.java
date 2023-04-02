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
	public PlayerProfilePO doConvert(PlayerProfile source) {
		return new PlayerProfilePO(
				source.getProfileId(),
				source.getProfileName(),
				source.getCharacterClass(),
				source.getRace(),
				source.getLevel(),
				source.getBuildId(),
				characterProfessionPOConverter.convertList(source.getProfessions().getList()),
				source.getEnemyType(),
				source.getPhase(),
				equipmentPOConverter.convert(source.getEquipment()),
				buffPOConverter.convertList(source.getBuffs().getList()),
				source.getLastModified()
		);
	}

	@Override
	public PlayerProfile doConvertBack(PlayerProfilePO source, Map<String, Object> params) {
		PlayerProfileService playerProfileService = getPlayerProfileService(params);

		PlayerProfile playerProfile = playerProfileService.createTemporaryPlayerProfile(
				source.getProfileId(),
				source.getProfileName(),
				source.getCharacterClassId(),
				source.getRace(),
				source.getLevel(),
				source.getBuildId(),
				characterProfessionPOConverter.convertBackList(source.getProfessions(), params),
				source.getEnemyType(),
				source.getPhaseId()
		);

		playerProfile.setEquipment(equipmentPOConverter.convertBack(source.getEquipment(), params));
		playerProfile.setBuffs(buffPOConverter.convertBackList(source.getBuffs(), params));
		playerProfile.setLastModified(source.getLastModified());
		return playerProfile;
	}
}
