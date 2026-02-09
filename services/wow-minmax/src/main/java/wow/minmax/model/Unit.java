package wow.minmax.model;

import wow.character.model.character.Character;

/**
 * User: POlszewski
 * Date: 2026-02-09
 */
public interface Unit extends Character {
	@Override
	Unit getTarget();
}
