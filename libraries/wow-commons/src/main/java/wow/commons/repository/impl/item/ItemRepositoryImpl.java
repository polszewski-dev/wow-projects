package wow.commons.repository.impl.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.impl.AbstractItemImpl;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.item.ItemExcelParser;
import wow.commons.repository.impl.parser.item.SourceParserFactory;
import wow.commons.repository.item.ItemRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.util.CollectionUtil;
import wow.commons.util.PhaseMap;

import javax.annotation.PostConstruct;
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
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
	private final SourceParserFactory sourceParserFactory;
	private final SpellRepository spellRepository;

	private final PhaseMap<Integer, Item> itemById = new PhaseMap<>();
	private final PhaseMap<String, List<Item>> itemByName = new PhaseMap<>();
	private final PhaseMap<ItemSlot, List<Item>> itemBySlot = new PhaseMap<>();

	private final PhaseMap<String, ItemSet> itemSetByName = new PhaseMap<>();

	@Value("${items.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<Item> getItem(int itemId, PhaseId phaseId) {
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

	@Override
	public Optional<ItemSet> getItemSet(String name, PhaseId phaseId) {
		return itemSetByName.getOptional(phaseId, name);
	}

	@PostConstruct
	public void init() throws IOException {
		var parser = new ItemExcelParser(xlsFilePath, sourceParserFactory, this, spellRepository);
		parser.readFromXls();
		for (Item item : itemById.allValues()) {
			((AbstractItemImpl) item).setFirstAppearedInPhase(getFirstAppearedInPhase(item));
		}
	}

	private PhaseId getFirstAppearedInPhase(Item item) {
		var earliestPhaseId = item.getEarliestPhaseId();

		if (earliestPhaseId.isPrepatch()) {
			var optionalPreviousPhase = earliestPhaseId.getPreviousPhase();

			if (optionalPreviousPhase.isPresent()) {
				var previousVersionsLastPhase = optionalPreviousPhase.get().getGameVersionId().getLastPhase();
				var optionalItem = itemById.getOptional(previousVersionsLastPhase, item.getId());

				if (optionalItem.isPresent()) {
					return getFirstAppearedInPhase(optionalItem.get());
				}
			}
		}

		return earliestPhaseId;
	}

	public void addItem(Item item) {
		putForEveryPhase(itemById, item.getId(), item);
		addEntryForEveryPhase(itemByName, item.getName(), item);
		for (var itemSlot : item.getItemType().getItemSlots()) {
			addEntryForEveryPhase(itemBySlot, itemSlot, item);
		}
	}

	public void addItemSet(ItemSet itemSet) {
		putForEveryPhase(itemSetByName, itemSet.getName(), itemSet);
	}
}
