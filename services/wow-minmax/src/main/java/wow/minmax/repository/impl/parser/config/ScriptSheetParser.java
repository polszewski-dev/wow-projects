package wow.minmax.repository.impl.parser.config;

import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.minmax.model.config.ScriptInfo;

/**
 * User: POlszewski
 * Date: 2025-09-26
 */
public class ScriptSheetParser extends WowExcelSheetParser {
	private final MinMaxConfigExcelParser parser;

	private final ExcelColumn colPath = column("path");

	protected ScriptSheetParser(String sheetName, MinMaxConfigExcelParser parser) {
		super(sheetName);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var scriptInfo = getScriptInfo();
		parser.add(scriptInfo);
	}

	private ScriptInfo getScriptInfo() {
		var characterRestriction = getRestriction();
		var timeRestriction = getTimeRestriction();
		var name = colName.getString();
		var path = colPath.getString();

		return new ScriptInfo(characterRestriction, timeRestriction, name, path);
	}
}
