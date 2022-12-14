package wow.commons.repository.impl.parsers.items;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.ItemRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@AllArgsConstructor
public class ItemExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final ItemRepositoryImpl itemRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new EnchantSheetParser("enchants", itemRepository)
		);
	}
}
