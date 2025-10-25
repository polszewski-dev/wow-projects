package wow.commons.repository.impl.parser.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemId;
import wow.commons.model.item.impl.AbstractItemImpl;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.ItemSetRepository;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.util.PhaseMap;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.ITEM;

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
	private final ItemSetRepository itemSetRepository;

	private final PhaseMap<ItemId, Item> itemsById = new PhaseMap<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ItemSheetParser(ITEM, sourceParserFactory, spellRepository, itemSetRepository, this)
		);
	}

	void addItem(Item item) {
		itemsById.put(item.getEarliestPhaseId(), item.getId(), item);
	}

	public List<Item> getItems() {
		for (var item : itemsById.allValues()) {
			((AbstractItemImpl<?>) item).setFirstAppearedInPhase(getFirstAppearedInPhase(item));
		}

		return itemsById.allValues();
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
