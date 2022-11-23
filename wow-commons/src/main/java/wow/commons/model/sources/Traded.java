package wow.commons.model.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.item.TradedItem;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
public class Traded extends Source {
	private final TradedItem sourceItem;

	@Override
	public boolean isTraded() {
		return true;
	}

	@Override
	public String toString() {
		return "Traded: " + sourceItem.getName();
	}
}
