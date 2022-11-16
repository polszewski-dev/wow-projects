package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.unit.CharacterInfo;
import wow.minmax.converter.Converter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.persistent.CharacterProfessionPO;
import wow.minmax.model.persistent.PlayerProfilePO;
import wow.minmax.repository.BuildRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class PlayerPOProfileConverter extends Converter<PlayerProfile, PlayerProfilePO> {
	private final EquipmentPOConverter equipmentPOConverter;
	private final CharacterProfessionPOConverter characterProfessionPOConverter;
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
				convertCharacterProfessions(playerProfile),
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
				convertBackCharacterInfo(value),
				value.getEnemyType(),
				value.getPhase(),
				buildRepository.getBuild(value.getBuildId(), value.getLevel()).orElseThrow()
		);

		playerProfile.setEquipment(equipmentPOConverter.convertBack(value.getEquipment()));
		playerProfile.setBuffs(buffPOConverter.convertBackList(value.getBuffs()));

		playerProfile.setLastModified(value.getLastModified());

		return playerProfile;
	}

	private List<CharacterProfessionPO> convertCharacterProfessions(PlayerProfile playerProfile) {
		return playerProfile.getCharacterInfo().getProfessions().stream()
				.map(characterProfessionPOConverter::convert)
				.collect(Collectors.toList());
	}

	private CharacterInfo convertBackCharacterInfo(PlayerProfilePO playerProfile) {
		return new CharacterInfo(
				playerProfile.getCharacterClass(),
				playerProfile.getRace(),
				playerProfile.getLevel(),
				playerProfile.getProfessions().stream()
						.map(characterProfessionPOConverter::doConvertBack)
						.collect(Collectors.toList())
		);
	}
}
