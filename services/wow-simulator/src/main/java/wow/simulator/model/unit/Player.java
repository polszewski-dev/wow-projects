package wow.simulator.model.unit;

import wow.character.model.character.PlayerCharacter;
import wow.commons.model.categorization.ItemSlot;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public interface Player extends Unit, PlayerCharacter {
	boolean canCast(ItemSlot itemSlot);

	void cast(ItemSlot itemSlot);
}
