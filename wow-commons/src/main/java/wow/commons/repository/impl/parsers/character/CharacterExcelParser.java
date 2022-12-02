package wow.commons.repository.impl.parsers.character;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.CharacterRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
public class CharacterExcelParser  extends ExcelParser {
	private final String xlsFilePath;
	private final CharacterRepositoryImpl characterRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new BaseStatsSheetParser("base_stats", characterRepository),
				new CombatRatingsSheetParser("combat_ratings", characterRepository),
				new BuildSheetParser("builds", characterRepository)
		);
	}
}
