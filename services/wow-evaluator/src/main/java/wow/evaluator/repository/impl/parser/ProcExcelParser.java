package wow.evaluator.repository.impl.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.evaluator.model.ProcInfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class ProcExcelParser extends ExcelParser {
	@Value("${proc.xls.file.path}")
	private final String xlsFilePath;

	@Getter
	private final List<ProcInfo> procInfos = new ArrayList<>();

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(new ProcSheetParser(Pattern.compile("proc_.+"), this));
	}

	void addProcInfo(ProcInfo procInfo) {
		procInfos.add(procInfo);
	}
}
