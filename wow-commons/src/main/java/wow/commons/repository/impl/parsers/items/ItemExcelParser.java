package wow.commons.repository.impl.parsers.items;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.ItemDataRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@AllArgsConstructor
public class ItemExcelParser extends ExcelParser {
	private final ItemDataRepositoryImpl itemDataRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/item_data.xls");
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new EnchantSheetParser("enchants", itemDataRepository)
		);
	}
}
