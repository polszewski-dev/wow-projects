package wow.commons.repository.impl.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.item.ItemExcelParser;
import wow.commons.repository.impl.parser.item.ItemSourceParserFactory;
import wow.commons.repository.item.ItemRepository;
import wow.commons.repository.item.ItemSetRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.util.CollectionUtil;
import wow.commons.util.PhaseMap;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static wow.commons.util.PhaseMap.addEntryForEveryPhase;
import static wow.commons.util.PhaseMap.putForEveryPhase;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@Component
public class ItemRepositoryImpl implements ItemRepository {
	private final PhaseMap<ItemId, Item> itemById = new PhaseMap<>();
	private final PhaseMap<String, List<Item>> itemByName = new PhaseMap<>();
	private final PhaseMap<ItemSlot, List<Item>> itemBySlot = new PhaseMap<>();

	public ItemRepositoryImpl(
			@Value("${items.xls.file.paths}") String[] xlsFilePaths,
			ItemSourceParserFactory itemSourceParserFactory,
			SpellRepository spellRepository,
			ItemSetRepository itemSetRepository
	) throws IOException {
		for (var xlsFilePath : xlsFilePaths) {
			var parser = new ItemExcelParser(
					xlsFilePath,
					itemSourceParserFactory,
					spellRepository,
					itemSetRepository
			);

			parser.readFromXls();
			parser.getItems().forEach(this::addItem);
		}
	}

	@Override
	public Optional<Item> getItem(ItemId itemId, PhaseId phaseId) {
		return itemById.getOptional(phaseId, itemId);
	}

	@Override
	public Optional<Item> getItem(String name, PhaseId phaseId) {
		return itemByName.getOptional(phaseId, name)
				.flatMap(CollectionUtil::getUniqueResult);
	}

	@Override
	public List<Item> getItemsBySlot(ItemSlot itemSlot, PhaseId phaseId) {
		return itemBySlot.getOptional(phaseId, itemSlot)
				.orElse(List.of());
	}

	private void addItem(Item item) {
		putForEveryPhase(itemById, item.getId(), item);
		addEntryForEveryPhase(itemByName, item.getName(), item);
		for (var itemSlot : item.getItemType().getItemSlots()) {
			addEntryForEveryPhase(itemBySlot, itemSlot, item);
		}
	}
}
