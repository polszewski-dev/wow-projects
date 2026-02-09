package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.ProfIdSpecId;
import wow.character.model.script.ScriptPathResolver;
import wow.character.service.CharacterService;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.FactionExclusionGroupId;
import wow.minmax.converter.model.PlayerCharacterConfigConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.ExclusiveFactionGroup;
import wow.minmax.model.Player;
import wow.minmax.model.config.ScriptInfo;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.model.impl.NonPlayerImpl;
import wow.minmax.model.impl.PlayerImpl;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.repository.PlayerCharacterConfigRepository;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerCharacterService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.groupingBy;
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
	public Player getPlayer(CharacterId characterId) {
		return getExistingOrNewCharacter(characterId);
	}

	private Player getExistingOrNewCharacter(CharacterId characterId) {
		return playerCharacterConfigRepository.findById(characterId.toString())
				.map(playerCharacterConfigConverter::convertBack)
				.orElseGet(() -> createCharacter(characterId));
	}

	private Player createCharacter(CharacterId characterId) {
		var profileId = characterId.profileId();
		var playerProfile = playerProfileRepository.findById(profileId.toString()).orElseThrow();

		var newCharacter = characterService.createPlayerCharacter(
				playerProfile.getProfileName(),
				playerProfile.getCharacterClassId(),
				playerProfile.getRaceId(),
				characterId.level(),
				characterId.phaseId(),
				PlayerImpl::new
		);

		var targetEnemy = characterService.createNonPlayerCharacter(
				"Target",
				characterId.enemyType(),
				newCharacter.getLevel() + characterId.enemyLevelDiff(),
				characterId.phaseId(),
				NonPlayerImpl::new
		);

		newCharacter.setTarget(targetEnemy);

		characterService.applyDefaultCharacterTemplate(newCharacter);

		return newCharacter;
	}

	@Override
	public void saveCharacter(CharacterId characterId, Player player) {
		var playerConfig = playerCharacterConfigConverter.convert(player, characterId);

		playerCharacterConfigRepository.save(playerConfig);

		var profileId = characterId.profileId();
		var profile = playerProfileRepository.findById(profileId.toString()).orElseThrow();

		profile.setLastModifiedCharacterId(characterId.toString());
		profile.setLastModified(LocalDateTime.now());

		playerProfileRepository.save(profile);
	}

	@Override
	public ViewConfig getViewConfig(Player player) {
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
	public Player changeProfession(CharacterId characterId, int index, ProfIdSpecId profession) {
		var player = getPlayer(characterId);

		player.setProfessionMaxLevel(index, profession);

		characterService.updateAfterRestrictionChange(player);
		saveCharacter(characterId, player);
		return player;
	}

	@Override
	public List<ExclusiveFactionGroup> getAvailableExclusiveFactions(CharacterId characterId) {
		var player = getPlayer(characterId);

		var factionsByGroup = player.getExclusiveFactions().getAvailable().stream()
				.collect(groupingBy(Faction::getExclusionGroupId));

		return factionsByGroup.entrySet().stream()
				.map(x -> getExclusiveFactionGroup(x.getKey(), x.getValue(), player))
				.toList();
	}

	private static ExclusiveFactionGroup getExclusiveFactionGroup(FactionExclusionGroupId groupId, List<Faction> availableFactions, Player player) {
		return new ExclusiveFactionGroup(
				groupId,
				player.getExclusiveFactions().get(groupId),
				availableFactions
		);
	}

	@Override
	public Player changeExclusiveFaction(CharacterId characterId, String factionName) {
		var player = getPlayer(characterId);

		player.getExclusiveFactions().enable(factionName);

		characterService.updateAfterRestrictionChange(player);
		saveCharacter(characterId, player);
		return player;
	}

	@Override
	public Player changeTalents(CharacterId characterId, String talentLink) {
		var player = getPlayer(characterId);

		player.getBuild().getTalents().loadFromTalentLink(talentLink);

		characterService.updateAfterRestrictionChange(player);
		saveCharacter(characterId, player);
		return player;
	}

	@Override
	public List<ScriptInfo> getAvailableScripts(CharacterId characterId) {
		var player = getPlayer(characterId);

		return minmaxConfigRepository.getAvailableScripts(player);
	}

	@Override
	public Player changeScript(CharacterId characterId, String scriptPath) {
		var player = getPlayer(characterId);

		requireExistingResource(scriptPath, player);

		player.getBuild().setScript(scriptPath);

		saveCharacter(characterId, player);
		return player;
	}

	private void requireExistingResource(String scriptPath, Player player) {
		var fullScriptPath = ScriptPathResolver.getScriptPath(scriptPath, player.getGameVersionId());
		var scriptURL = getClass().getResource(fullScriptPath);

		Objects.requireNonNull(scriptURL, "No script %s, full path: %s".formatted(scriptPath, fullScriptPath));
	}
}
