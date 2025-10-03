package wow.minmax.service;

import wow.character.model.character.CharacterProfession;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.ProfIdSpecId;
import wow.minmax.model.CharacterId;
import wow.minmax.model.ExclusiveFactionGroup;
import wow.minmax.model.config.ScriptInfo;
import wow.minmax.model.config.ViewConfig;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
public interface PlayerCharacterService {
	PlayerCharacter getPlayer(CharacterId characterId);

	void saveCharacter(CharacterId characterId, PlayerCharacter player);

	ViewConfig getViewConfig(PlayerCharacter player);

	List<CharacterProfession> getAvailableProfessions(CharacterId characterId);

	PlayerCharacter changeProfession(CharacterId characterId, int index, ProfIdSpecId profession);

	List<ExclusiveFactionGroup> getAvailableExclusiveFactions(CharacterId characterId);

	PlayerCharacter changeExclusiveFaction(CharacterId characterId, String factionName);

	List<ScriptInfo> getAvailableScripts(CharacterId characterId);

	PlayerCharacter changeScript(CharacterId characterId, String scriptPath);
}
