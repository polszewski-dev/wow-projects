package wow.simulator.model.unit;

import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.model.categorization.ItemSlot;
import wow.simulator.model.unit.impl.PlayerImpl;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface Player extends Unit, PlayerCharacter {
	boolean canCast(ItemSlot itemSlot);

	void cast(ItemSlot itemSlot);

	static PlayerCharacterFactory<Player> getFactory(String name) {
		return (phase, characterClass, race, level, baseStatInfo , combatRatingInfo, talents) ->
				new PlayerImpl(
						name,
						new PlayerCharacterImpl(phase, characterClass, race, level, baseStatInfo, combatRatingInfo, talents)
				);
	}
}
