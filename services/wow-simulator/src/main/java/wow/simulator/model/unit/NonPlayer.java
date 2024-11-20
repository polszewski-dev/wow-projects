package wow.simulator.model.unit;

import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.impl.NonPlayerCharacterImpl;
import wow.character.service.NonPlayerCharacterFactory;
import wow.simulator.model.unit.impl.NonPlayerImpl;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface NonPlayer extends Unit, NonPlayerCharacter {
	static NonPlayerCharacterFactory<NonPlayer> getFactory(String name) {
		return (phase, characterClass, creatureType, level, combatRatingInfo) ->
				new NonPlayerImpl(
						name,
						new NonPlayerCharacterImpl(phase, characterClass, creatureType, level, combatRatingInfo)
				);
	}
}
