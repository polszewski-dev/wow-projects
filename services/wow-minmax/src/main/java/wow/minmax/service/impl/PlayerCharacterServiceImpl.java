package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.ProfIdSpecId;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.service.CharacterService;
import wow.minmax.converter.model.PlayerCharacterConfigConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.repository.PlayerCharacterConfigRepository;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerCharacterService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static wow.commons.model.profession.ProfessionType.SECONDARY;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
@Service
@AllArgsConstructor
public class PlayerCharacterServiceImpl implements PlayerCharacterService {
	private final CharacterService characterService;
	private final MinmaxConfigRepository minmaxConfigRepository;

	private final PlayerCharacterConfigRepository playerCharacterConfigRepository;
	private final PlayerCharacterConfigConverter playerCharacterConfigConverter;

	private final PlayerProfileRepository playerProfileRepository;

	@Override
	public PlayerCharacter getPlayer(CharacterId characterId) {
		return getExistingOrNewCharacter(characterId);
	}

	private PlayerCharacter getExistingOrNewCharacter(CharacterId characterId) {
		return playerCharacterConfigRepository.findById(characterId.toString())
				.map(playerCharacterConfigConverter::convertBack)
				.orElseGet(() -> createCharacter(characterId));
	}

	private PlayerCharacter createCharacter(CharacterId characterId) {
		var profileId = characterId.profileId();
		var playerProfile = playerProfileRepository.findById(profileId.toString()).orElseThrow();

		var newCharacter = characterService.createPlayerCharacter(
				playerProfile.getProfileName(),
				playerProfile.getCharacterClassId(),
				playerProfile.getRaceId(),
				characterId.level(),
				characterId.phaseId(),
				PlayerCharacterImpl::new
		);

		var targetEnemy = characterService.createNonPlayerCharacter(
				"Target",
				characterId.enemyType(),
				newCharacter.getLevel() + characterId.enemyLevelDiff(),
				characterId.phaseId(),
				NonPlayerCharacterImpl::new
		);

		newCharacter.setTarget(targetEnemy);

		characterService.applyDefaultCharacterTemplate(newCharacter);

		return newCharacter;
	}

	@Override
	public void saveCharacter(CharacterId characterId, PlayerCharacter player) {
		var playerConfig = playerCharacterConfigConverter.convert(player, characterId);

		playerCharacterConfigRepository.save(playerConfig);

		var profileId = characterId.profileId();
		var profile = playerProfileRepository.findById(profileId.toString()).orElseThrow();

		profile.setLastModifiedCharacterId(characterId.toString());
		profile.setLastModified(LocalDateTime.now());

		playerProfileRepository.save(profile);
	}

	@Override
	public ViewConfig getViewConfig(PlayerCharacter player) {
		return minmaxConfigRepository.getViewConfig(player).orElseThrow();
	}

	@Override
	public List<CharacterProfession> getAvailableProfessions(CharacterId characterId) {
		var player = getPlayer(characterId);
		var result = new ArrayList<CharacterProfession>();

		for (var profession : player.getGameVersion().getProfessions()) {
			if (profession.getType() == SECONDARY) {
				continue;
			}

			result.add(new CharacterProfession(profession, null, 1));

			for (var specialization : profession.getSpecializations()) {
				result.add(new CharacterProfession(profession, specialization, 1));
			}
		}

		return result;
	}

	@Override
	public PlayerCharacter changeProfession(CharacterId characterId, int index, ProfIdSpecId profession) {
		var player = getPlayer(characterId);

		player.setProfessionMaxLevel(index, profession);

		characterService.updateAfterRestrictionChange(player);
		saveCharacter(characterId, player);
		return player;
	}
}
