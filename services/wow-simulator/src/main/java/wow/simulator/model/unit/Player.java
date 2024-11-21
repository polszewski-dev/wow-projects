package wow.simulator.model.unit;

import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.service.PlayerCharacterFactory;
import wow.simulator.model.unit.impl.PlayerImpl;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface Player extends Unit, PlayerCharacter {
	static PlayerCharacterFactory<Player> getFactory(String name) {
		return (phase, characterClass, race, level, baseStatInfo , combatRatingInfo, talents) ->
				new PlayerImpl(
						name,
						new PlayerCharacterImpl(phase, characterClass, race, level, baseStatInfo, combatRatingInfo, talents)
				);
	}
}
