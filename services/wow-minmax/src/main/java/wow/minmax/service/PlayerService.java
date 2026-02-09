package wow.minmax.service;

import wow.character.model.character.CharacterProfession;
import wow.character.model.character.ProfIdSpecId;
import wow.minmax.model.CharacterId;
import wow.minmax.model.ExclusiveFactionGroup;
import wow.minmax.model.Player;
import wow.minmax.model.config.ScriptInfo;
import wow.minmax.model.config.ViewConfig;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
public interface PlayerService {
	Player getPlayer(CharacterId characterId);

	void saveCharacter(CharacterId characterId, Player player);

	ViewConfig getViewConfig(Player player);

	List<CharacterProfession> getAvailableProfessions(CharacterId characterId);

	Player changeProfession(CharacterId characterId, int index, ProfIdSpecId profession);

	List<ExclusiveFactionGroup> getAvailableExclusiveFactions(CharacterId characterId);

	Player changeExclusiveFaction(CharacterId characterId, String factionName);

	Player changeTalents(CharacterId characterId, String talentLink);

	List<ScriptInfo> getAvailableScripts(CharacterId characterId);

	Player changeScript(CharacterId characterId, String scriptPath);
}
