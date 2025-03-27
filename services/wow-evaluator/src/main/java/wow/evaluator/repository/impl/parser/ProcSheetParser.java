package wow.evaluator.repository.impl.parser;

import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.evaluator.model.ProcInfo;

import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2023-05-03
 */
public class ProcSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colChance = column("chance");
	private final ExcelColumn colCastTime = column("cast_time");
	private final ExcelColumn colDuration = column("duration");
	private final ExcelColumn colInternalCd = column("internal_cd");
	private final ExcelColumn colAvgUptime = column("avg_uptime");

	private final ProcExcelParser parser;

	protected ProcSheetParser(Pattern sheetNamePattern, ProcExcelParser parser) {
		super(sheetNamePattern);
		this.parser = parser;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colChance;
	}

	@Override
	protected void readSingleRow() {
		var procInfo = getProcInfo();
		parser.addProcInfo(procInfo);
	}

	private ProcInfo getProcInfo() {
		var chance = colChance.getInteger();
		var castTime = colCastTime.getInteger();
		var duration = colDuration.getInteger();
		var internalCd = colInternalCd.getInteger();
		var avgUptime = colAvgUptime.getDouble();

		return new ProcInfo(
				chance,
				castTime,
				duration,
				internalCd,
				avgUptime
		);
	}
}
