package wow.commons.repository.impl.parser.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.item.ItemSet;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.util.PhaseMap;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.SET;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class ItemSetExcelParser extends ExcelParser {
	@Value("${item.sets.xls.file.path}")
	private final String xlsFilePath;

	private final SourceParserFactory sourceParserFactory;
	private final SpellRepository spellRepository;

	private final PhaseMap<String, ItemSet> itemSetsByName = new PhaseMap<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ItemSetSheetParser(SET, sourceParserFactory, spellRepository, this)
		);
	}

	void addItemSet(ItemSet itemSet) {
		itemSetsByName.put(itemSet.getEarliestPhaseId(), itemSet.getName(), itemSet);
	}

	public List<ItemSet> getItemSets() {
		return itemSetsByName.allValues();
	}
}
