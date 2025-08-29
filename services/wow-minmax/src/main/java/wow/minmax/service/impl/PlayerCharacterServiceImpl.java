package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.BuffListType;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.service.CharacterService;
import wow.commons.model.buff.BuffId;
import wow.minmax.converter.model.PlayerCharacterConfigConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.repository.PlayerCharacterConfigRepository;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerCharacterService;

import java.time.LocalDateTime;

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
	public PlayerCharacter enableBuff(CharacterId characterId, BuffListType buffListType, BuffId buffId, int rank, boolean enabled) {
		var player = getPlayer(characterId);

		player.getBuffList(buffListType).enable(buffId, rank, enabled);
		saveCharacter(characterId, player);
		return player;
	}

	@Override
	public PlayerCharacter enableConsumable(CharacterId characterId, String consumableName, boolean enabled) {
		var player = getPlayer(characterId);

		player.getConsumables().enable(consumableName, enabled);
		saveCharacter(characterId, player);
		return player;
	}

	@Override
	public ViewConfig getViewConfig(PlayerCharacter player) {
		return minmaxConfigRepository.getViewConfig(player).orElseThrow();
	}
}
