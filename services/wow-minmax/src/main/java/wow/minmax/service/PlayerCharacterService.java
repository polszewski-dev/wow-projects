package wow.minmax.service;

import wow.character.model.character.PlayerCharacter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.config.ViewConfig;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
public interface PlayerCharacterService {
	PlayerCharacter getPlayer(CharacterId characterId);

	void saveCharacter(CharacterId characterId, PlayerCharacter player);

	PlayerCharacter enableConsumable(CharacterId characterId, String consumableName, boolean enabled);

	ViewConfig getViewConfig(PlayerCharacter player);
}
