package wow.minmax.service;

import wow.character.model.character.CharacterProfession;
import wow.character.model.character.ProfIdSpecId;
import wow.character.model.character.Raid;
import wow.minmax.model.ExclusiveFactionGroup;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;
import wow.minmax.model.config.ScriptInfo;
import wow.minmax.model.config.ViewConfig;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
public interface PlayerService {
	Player getPlayer(PlayerId playerId);

	Raid<Player> getRaid(PlayerId playerId);

	Raid<Player> getRaid(Player player);

	void savePlayer(Player player);

	ViewConfig getViewConfig(Player player);

	List<CharacterProfession> getAvailableProfessions(PlayerId playerId);

	Player changeProfession(PlayerId playerId, int index, ProfIdSpecId profession);

	List<ExclusiveFactionGroup> getAvailableExclusiveFactions(PlayerId playerId);

	Player changeExclusiveFaction(PlayerId playerId, String factionName);

	Player changeTalents(PlayerId playerId, String talentLink);

	List<ScriptInfo> getAvailableScripts(PlayerId playerId);

	Player changeScript(PlayerId playerId, String scriptPath);
}
