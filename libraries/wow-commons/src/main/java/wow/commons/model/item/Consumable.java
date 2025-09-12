package wow.commons.model.item;

import wow.commons.model.spell.ActivatedAbility;

/**
 * User: POlszewski
 * Date: 2024-11-22
 */
public interface Consumable extends AbstractItem<ConsumableId> {
	ActivatedAbility getActivatedAbility();
}
