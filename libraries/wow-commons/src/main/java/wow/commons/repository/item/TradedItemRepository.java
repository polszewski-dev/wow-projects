package wow.commons.repository.item;

import wow.commons.model.item.TradedItem;
import wow.commons.model.item.TradedItemId;
import wow.commons.model.pve.PhaseId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public interface TradedItemRepository {
	Optional<TradedItem> getTradedItem(TradedItemId tradedItemId, PhaseId phaseId);
}
