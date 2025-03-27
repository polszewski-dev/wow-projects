package wow.commons.repository.impl.parser.character;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.pve.GameVersionRepository;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class ProfessionExcelParser extends ExcelParser {
	@Value("${professions.xls.file.path}")
	private final String xlsFilePath;

	private final GameVersionRepository gameVersionRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ProfessionSheetParser("professions", gameVersionRepository),
				new ProfessionSpecSheetParser("profession_specs", gameVersionRepository),
				new ProfessionProficiencySheetParser("profession_proficiencies", gameVersionRepository)
		);
	}
}
