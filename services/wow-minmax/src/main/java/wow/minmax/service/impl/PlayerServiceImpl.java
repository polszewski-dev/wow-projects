package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.ProfIdSpecId;
import wow.character.service.CharacterService;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.FactionExclusionGroupId;
import wow.minmax.converter.db.PlayerConfigConverter;
import wow.minmax.model.ExclusiveFactionGroup;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;
import wow.minmax.model.config.ScriptInfo;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.model.impl.NonPlayerImpl;
import wow.minmax.model.impl.PlayerImpl;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.repository.PlayerConfigRepository;
import wow.minmax.repository.PlayerProfileRepository;
import wow.minmax.service.PlayerService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static wow.character.model.script.ScriptPathResolver.requireExistingScriptFile;
import static wow.commons.model.profession.ProfessionType.SECONDARY;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
@Service
@AllArgsConstructor
public class PlayerServiceImpl implements PlayerService {
	private final CharacterService characterService;
	private final MinmaxConfigRepository minmaxConfigRepository;

	private final PlayerConfigRepository playerConfigRepository;
	private final PlayerConfigConverter playerConfigConverter;

	private final PlayerProfileRepository playerProfileRepository;

	@Override
	public Player getPlayer(PlayerId playerId) {
		return getExistingOrNewPlayer(playerId);
	}

	private Player getExistingOrNewPlayer(PlayerId playerId) {
		return playerConfigRepository.findById(playerId.toString())
				.map(playerConfigConverter::convertBack)
				.orElseGet(() -> createPlayer(playerId));
	}

	private Player createPlayer(PlayerId playerId) {
		var profileId = playerId.profileId();
		var playerProfile = playerProfileRepository.findById(profileId.toString()).orElseThrow();

		var newPlayer = characterService.createPlayerCharacter(
				playerProfile.getProfileName(),
				playerProfile.getCharacterClassId(),
				playerProfile.getRaceId(),
				playerId.level(),
				playerId.phaseId(),
				PlayerImpl::new
		);

		var targetEnemy = characterService.createNonPlayerCharacter(
				"Target",
				playerId.enemyType(),
				newPlayer.getLevel() + playerId.enemyLevelDiff(),
				playerId.phaseId(),
				NonPlayerImpl::new
		);

		newPlayer.setTarget(targetEnemy);

		characterService.applyDefaultCharacterTemplate(newPlayer);

		return newPlayer;
	}

	@Override
	public void savePlayer(PlayerId playerId, Player player) {
		var playerConfig = playerConfigConverter.convert(player, playerId);

		playerConfigRepository.save(playerConfig);

		var profileId = playerId.profileId();
		var profile = playerProfileRepository.findById(profileId.toString()).orElseThrow();

		profile.setLastModifiedPlayerId(playerId.toString());
		profile.setLastModified(LocalDateTime.now());

		playerProfileRepository.save(profile);
	}

	@Override
	public ViewConfig getViewConfig(Player player) {
		return minmaxConfigRepository.getViewConfig(player).orElseThrow();
	}

	@Override
	public List<CharacterProfession> getAvailableProfessions(PlayerId playerId) {
		var player = getPlayer(playerId);
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
	public Player changeProfession(PlayerId playerId, int index, ProfIdSpecId profession) {
		var player = getPlayer(playerId);

		player.setProfessionMaxLevel(index, profession);

		characterService.updateAfterRestrictionChange(player);
		savePlayer(playerId, player);
		return player;
	}

	@Override
	public List<ExclusiveFactionGroup> getAvailableExclusiveFactions(PlayerId playerId) {
		var player = getPlayer(playerId);

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
	public Player changeExclusiveFaction(PlayerId playerId, String factionName) {
		var player = getPlayer(playerId);

		player.getExclusiveFactions().enable(factionName);

		characterService.updateAfterRestrictionChange(player);
		savePlayer(playerId, player);
		return player;
	}

	@Override
	public Player changeTalents(PlayerId playerId, String talentLink) {
		var player = getPlayer(playerId);

		player.getBuild().getTalents().loadFromTalentLink(talentLink);

		characterService.updateAfterRestrictionChange(player);
		savePlayer(playerId, player);
		return player;
	}

	@Override
	public List<ScriptInfo> getAvailableScripts(PlayerId playerId) {
		var player = getPlayer(playerId);

		return minmaxConfigRepository.getAvailableScripts(player);
	}

	@Override
	public Player changeScript(PlayerId playerId, String scriptPath) {
		var player = getPlayer(playerId);

		requireExistingScriptFile(scriptPath, player.getGameVersionId());

		player.getBuild().setScript(scriptPath);

		savePlayer(playerId, player);
		return player;
	}
}
