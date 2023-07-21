package wow.commons.model.sources;

import wow.commons.model.item.TradedItem;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public record Traded(TradedItem sourceItem) implements Source {
	@Override
	public boolean isTraded() {
		return true;
	}

	@Override
	public String toString() {
		return "Traded: " + sourceItem.getName();
	}
}
