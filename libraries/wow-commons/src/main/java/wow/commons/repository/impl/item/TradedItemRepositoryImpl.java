package wow.commons.repository.impl.item;

import org.springframework.stereotype.Component;
import wow.commons.model.item.TradedItem;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.item.TradedItemExcelParser;
import wow.commons.repository.item.TradedItemRepository;
import wow.commons.util.PhaseMap;

import java.io.IOException;
import java.util.Optional;

import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@Component
public class TradedItemRepositoryImpl implements TradedItemRepository {
	private final PhaseMap<Integer, TradedItem> tradedItemById = new PhaseMap<>();

	public TradedItemRepositoryImpl(TradedItemExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getTradedItems().forEach(this::addTradedItem);
	}

	@Override
	public Optional<TradedItem> getTradedItem(int tradedItemId, PhaseId phaseId) {
		return tradedItemById.getOptional(phaseId, tradedItemId);
	}

	private void addTradedItem(TradedItem tradedItem) {
		putForEveryPhase(tradedItemById, tradedItem.getId(), tradedItem);
	}
}
