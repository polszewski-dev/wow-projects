package wow.character.model.snapshot;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spell.Cost;
import wow.commons.model.spell.ResourceType;

/**
 * User: POlszewski
 * Date: 2023-10-20
 */
@Getter
@Setter
public class SpellCostSnapshot {
	private ResourceType resourceType;
	private int cost;
	private int costUnreduced;
	private double cooldown;

	public Cost getCostToPay() {
		return new Cost(resourceType, cost);
	}

	public Cost getCostToPayUnreduced() {
		return new Cost(resourceType, costUnreduced);
	}
}
