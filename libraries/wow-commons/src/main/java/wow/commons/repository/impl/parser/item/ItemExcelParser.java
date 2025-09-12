package wow.commons.repository.impl.parser.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemId;
import wow.commons.model.item.ItemSet;
import wow.commons.model.item.impl.AbstractItemImpl;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.util.PhaseMap;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.ITEM;
import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.SET;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class ItemExcelParser extends ExcelParser {
	@Value("${items.xls.file.path}")
	private final String xlsFilePath;

	private final SourceParserFactory sourceParserFactory;
	private final SpellRepository spellRepository;

	private final PhaseMap<ItemId, Item> itemsById = new PhaseMap<>();
	private final PhaseMap<String, ItemSet> itemSetsByName = new PhaseMap<>();

	private record ItemSetPieceKey(String name, GameVersionId version) {}

	private final Map<ItemSetPieceKey, List<Item>> setPiecesByName = new HashMap<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ItemSheetParser(ITEM, sourceParserFactory, spellRepository, this),
				new ItemSetSheetParser(SET, sourceParserFactory, spellRepository, this)
		);
	}

	void addItem(Item item) {
		itemsById.put(item.getEarliestPhaseId(), item.getId(), item);
	}

	void addItemSetPiece(String itemSetName, Item item) {
		var key = new ItemSetPieceKey(itemSetName, item.getGameVersionId());
		setPiecesByName.computeIfAbsent(key, x -> new ArrayList<>()).add(item);
	}

	void addItemSet(ItemSet itemSet) {
		itemSetsByName.put(itemSet.getEarliestPhaseId(), itemSet.getName(), itemSet);
	}

	List<Item> getPieces(String name, GameVersionId version) {
		var key = new ItemSetPieceKey(name, version);
		return setPiecesByName.getOrDefault(key, List.of());
	}

	public List<Item> getItems() {
		for (var item : itemsById.allValues()) {
			((AbstractItemImpl<?>) item).setFirstAppearedInPhase(getFirstAppearedInPhase(item));
		}

		return itemsById.allValues();
	}

	public List<ItemSet> getItemSets() {
		return itemSetsByName.allValues();
	}

	private PhaseId getFirstAppearedInPhase(Item item) {
		var earliestPhaseId = item.getEarliestPhaseId();

		if (earliestPhaseId.isPrepatch()) {
			var previousPhase = earliestPhaseId.getPreviousPhase().orElseThrow();
			var previousVersionsLastPhase = previousPhase.getGameVersionId().getLastPhase();
			var optionalItem = itemsById.getOptional(previousVersionsLastPhase, item.getId());

			if (optionalItem.isPresent()) {
				return getFirstAppearedInPhase(optionalItem.get());
			}
		}

		return earliestPhaseId;
	}
}
