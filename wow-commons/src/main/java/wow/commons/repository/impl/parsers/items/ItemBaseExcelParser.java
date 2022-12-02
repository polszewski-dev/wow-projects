package wow.commons.repository.impl.parsers.items;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.item.Item;
import wow.commons.repository.PveRepository;
import wow.commons.repository.impl.ItemDataRepositoryImpl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parsers.items.ItemBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@AllArgsConstructor
public class ItemBaseExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final ItemDataRepositoryImpl itemDataRepository;
	private final PveRepository pveRepository;

	private final Map<String, List<Item>> setPiecesByName = new HashMap<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new TradedItemSheetParser(TRADE, pveRepository, itemDataRepository),
				new ItemSheetParser(ITEM, pveRepository, itemDataRepository, setPiecesByName),
				new GemSheetParser(GEM, pveRepository, itemDataRepository),
				new ItemSetSheetParser(SET, itemDataRepository, setPiecesByName)
		);
	}
}
