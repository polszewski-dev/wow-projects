package wow.minmax.model;

import wow.character.model.character.Character;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
public interface Unit extends Character {
	@Override
	Unit getTarget();
}
