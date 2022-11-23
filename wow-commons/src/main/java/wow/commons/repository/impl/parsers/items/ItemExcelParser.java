package wow.commons.repository.impl.parsers.items;

import lombok.AllArgsConstructor;
import wow.commons.repository.impl.ItemDataRepositoryImpl;
import wow.commons.util.ExcelParser;
import wow.commons.util.ExcelSheetReader;

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
	protected Stream<ExcelSheetReader> getSheetReaders() {
		return Stream.of(
				new EnchantSheetReader("enchants", itemDataRepository)
		);
	}
}
