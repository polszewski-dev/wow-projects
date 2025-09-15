package polszewski.excel.reader.templates;

import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2025-09-16
 */
public final class NullExcelSheetParser extends ExcelSheetParser {
	public NullExcelSheetParser(String sheetName) {
		super(sheetName);
	}

	public NullExcelSheetParser(Pattern sheetNamePattern) {
		super(sheetNamePattern);
	}

	@Override
	public void readSheet() {
		// void
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return null;
	}

	@Override
	protected void readSingleRow() {
		// void
	}
}
