package wow.minmax.repository.impl.parser.proc;

import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.minmax.model.ProcInfo;
import wow.minmax.repository.impl.ProcInfoRepositoryImpl;

import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2023-05-03
 */
public class ProcSheetParser extends WowExcelSheetParser {
	private final ProcInfoRepositoryImpl procInfoRepository;

	private final ExcelColumn colChance = column("chance");
	private final ExcelColumn colCastTime = column("cast_time");
	private final ExcelColumn colDuration = column("duration");
	private final ExcelColumn colInternalCd = column("internal_cd");
	private final ExcelColumn colAvgUptime = column("avg_uptime");

	protected ProcSheetParser(Pattern sheetNamePattern, ProcInfoRepositoryImpl procInfoRepository) {
		super(sheetNamePattern);
		this.procInfoRepository = procInfoRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colChance;
	}

	@Override
	protected void readSingleRow() {
		ProcInfo procInfo = getProcInfo();
		procInfoRepository.addProcInfo(procInfo);
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
