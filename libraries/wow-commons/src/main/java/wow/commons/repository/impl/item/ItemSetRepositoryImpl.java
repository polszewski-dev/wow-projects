package wow.commons.repository.impl.item;

import org.springframework.stereotype.Component;
import wow.commons.model.item.ItemSet;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.item.ItemSetExcelParser;
import wow.commons.repository.item.ItemSetRepository;
import wow.commons.util.PhaseMap;

import java.io.IOException;
import java.util.Optional;

import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2025-10-25
 */
@Component
public class ItemSetRepositoryImpl implements ItemSetRepository {
	private final PhaseMap<String, ItemSet> itemSetByName = new PhaseMap<>();

	public ItemSetRepositoryImpl(ItemSetExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getItemSets().forEach(this::addItemSet);
	}

	@Override
	public Optional<ItemSet> getItemSet(String name, PhaseId phaseId) {
		return itemSetByName.getOptional(phaseId, name);
	}

	private void addItemSet(ItemSet itemSet) {
		putForEveryPhase(itemSetByName, itemSet.getName(), itemSet);
	}
}
