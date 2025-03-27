package wow.commons.repository.impl.parser.character;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.pve.GameVersionRepository;
import wow.commons.repository.spell.SpellRepository;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class RaceExcelParser extends ExcelParser {
	@Value("${races.xls.file.path}")
	private final String xlsFilePath;

	private final GameVersionRepository gameVersionRepository;
	private final SpellRepository spellRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new RaceSheetParser("races", gameVersionRepository, spellRepository)
		);
	}
}
